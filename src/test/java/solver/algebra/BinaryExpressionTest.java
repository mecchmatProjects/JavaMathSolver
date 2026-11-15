package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import solver.algebra.BinaryExpression.Operator;

class BinaryExpressionTest {

    private static final double EPS = 1e-9;


    private static Expression constant(double value) {
        return new ConstantExpression(value);
    }

    private static Expression variable(String name) {
        return new VariableExpression(name);
    }

    private final Expression a = variable("a");
    private final Expression b = variable("b");
    private final Expression c = variable("c");
    private final Expression d = variable("d");

    private static BinaryExpression bin(Expression left, Operator op, Expression right) {
        return new BinaryExpression(left, op, right);
    }


    @Nested
    @DisplayName("Operator")
    class OperatorTests {

        @Test
        @DisplayName("getSymbol: символи операторів")
        void symbols() {
            assertEquals("+", Operator.ADD.getSymbol());
            assertEquals("-", Operator.SUBTRACT.getSymbol());
            assertEquals("*", Operator.MULTIPLY.getSymbol());
            assertEquals("/", Operator.DIVIDE.getSymbol());
        }

        @Test
        @DisplayName("getPrecedence: + і - мають 1, * і / мають 2")
        void precedences() {
            assertEquals(1, Operator.ADD.getPrecedence());
            assertEquals(1, Operator.SUBTRACT.getPrecedence());
            assertEquals(2, Operator.MULTIPLY.getPrecedence());
            assertEquals(2, Operator.DIVIDE.getPrecedence());
        }

        @Test
        @DisplayName("fromSymbol: розпізнає всі чотири символи")
        void fromSymbolAll() {
            assertSame(Operator.ADD, Operator.fromSymbol("+"));
            assertSame(Operator.SUBTRACT, Operator.fromSymbol("-"));
            assertSame(Operator.MULTIPLY, Operator.fromSymbol("*"));
            assertSame(Operator.DIVIDE, Operator.fromSymbol("/"));
        }

        @Test
        @DisplayName("fromSymbol(getSymbol()) повертає той самий оператор")
        void fromSymbolRoundTrip() {
            for (Operator op : Operator.values()) {
                assertSame(op, Operator.fromSymbol(op.getSymbol()));
            }
        }

        @Test
        @DisplayName("fromSymbol: пробіли навколо символу ігноруються")
        void fromSymbolTrims() {
            assertSame(Operator.MULTIPLY, Operator.fromSymbol("  *  "));
            assertSame(Operator.ADD, Operator.fromSymbol(" +"));
        }

        @Test
        @DisplayName("fromSymbol(null) → IllegalArgumentException")
        void fromSymbolNull() {
            assertThrows(IllegalArgumentException.class, () -> Operator.fromSymbol(null));
        }

        @Test
        @DisplayName("fromSymbol: непідтримуваний символ → IllegalArgumentException з символом у повідомленні")
        void fromSymbolUnsupported() {
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> Operator.fromSymbol("%"));
            assertTrue(ex.getMessage().contains("%"));
            assertThrows(IllegalArgumentException.class, () -> Operator.fromSymbol("^"));
            assertThrows(IllegalArgumentException.class, () -> Operator.fromSymbol(""));
            assertThrows(IllegalArgumentException.class, () -> Operator.fromSymbol("++"));
        }
    }


    @Nested
    @DisplayName("Конструктори та геттери")
    class ConstructorTests {

        @Test
        @DisplayName("Геттери повертають ті самі об'єкти, що передали в конструктор")
        void getters() {
            BinaryExpression expr = bin(a, Operator.ADD, b);
            assertSame(a, expr.getLeft());
            assertSame(Operator.ADD, expr.getOperator());
            assertSame(b, expr.getRight());
        }

        @Test
        @DisplayName("null-операнди та null-оператор → IllegalArgumentException")
        void nullArguments() {
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(null, Operator.ADD, b));
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(a, Operator.ADD, null));
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(a, (Operator) null, b));
        }

        @Test
        @DisplayName("Конструктор зі String: символ перетворюється на Operator")
        void stringConstructor() {
            BinaryExpression expr = new BinaryExpression(a, "*", b);
            assertSame(Operator.MULTIPLY, expr.getOperator());
            assertSame(a, expr.getLeft());
            assertSame(b, expr.getRight());
        }

        @Test
        @DisplayName("Конструктор зі String: пробіли ігноруються")
        void stringConstructorTrims() {
            assertSame(Operator.SUBTRACT, new BinaryExpression(a, " - ", b).getOperator());
        }

        @Test
        @DisplayName("Конструктор зі String: null та невідомий символ → IllegalArgumentException")
        void stringConstructorInvalid() {
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(a, (String) null, b));
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(a, "%", b));
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(null, "+", b));
            assertThrows(IllegalArgumentException.class, () -> new BinaryExpression(a, "+", null));
        }
    }


    @Nested
    @DisplayName("evaluate")
    class EvaluateTests {

        private final Map<String, Double> noVars = Map.of();

        @Test
        @DisplayName("Чотири операції над константами")
        void basicOperations() {
            assertEquals(5.0, bin(constant(2), Operator.ADD, constant(3)).evaluate(noVars), EPS);
            assertEquals(4.0, bin(constant(7), Operator.SUBTRACT, constant(3)).evaluate(noVars), EPS);
            assertEquals(20.0, bin(constant(4), Operator.MULTIPLY, constant(5)).evaluate(noVars), EPS);
            assertEquals(2.5, bin(constant(10), Operator.DIVIDE, constant(4)).evaluate(noVars), EPS);
        }

        @Test
        @DisplayName("Від'ємні числа та нуль")
        void negativesAndZero() {
            assertEquals(-1.0, bin(constant(2), Operator.ADD, constant(-3)).evaluate(noVars), EPS);
            assertEquals(6.0, bin(constant(-2), Operator.MULTIPLY, constant(-3)).evaluate(noVars), EPS);
            assertEquals(0.0, bin(constant(0), Operator.DIVIDE, constant(5)).evaluate(noVars), EPS);
            assertEquals(0.0, bin(constant(5), Operator.MULTIPLY, constant(0)).evaluate(noVars), EPS);
        }

        @Test
        @DisplayName("Змінні: x + 5 при x = 2 → 7")
        void withVariable() {
            BinaryExpression expr = bin(variable("x"), Operator.ADD, constant(5));
            assertEquals(7.0, expr.evaluate(Map.of("x", 2.0)), EPS);
        }

        @Test
        @DisplayName("Змінні: (x + 2) / y при x = 4, y = 3 → 2")
        void nestedWithVariables() {
            Expression numerator = bin(variable("x"), Operator.ADD, constant(2));
            BinaryExpression expr = bin(numerator, Operator.DIVIDE, variable("y"));
            assertEquals(2.0, expr.evaluate(Map.of("x", 4.0, "y", 3.0)), EPS);
        }

        @Test
        @DisplayName("Вкладені вирази: (x + 2) * (y - 1) при x = 1, y = 4 → 9")
        void bothSidesNested() {
            Expression left = bin(variable("x"), Operator.ADD, constant(2));
            Expression right = bin(variable("y"), Operator.SUBTRACT, constant(1));
            BinaryExpression expr = bin(left, Operator.MULTIPLY, right);
            assertEquals(9.0, expr.evaluate(Map.of("x", 1.0, "y", 4.0)), EPS);
        }

        @Test
        @DisplayName("Структура дерева визначає порядок: (10 - 4) - 3 = 3, але 10 - (4 - 3) = 9")
        void treeStructureDefinesOrder() {
            Expression tenMinusFour = bin(constant(10), Operator.SUBTRACT, constant(4));
            Expression fourMinusThree = bin(constant(4), Operator.SUBTRACT, constant(3));

            assertEquals(3.0, bin(tenMinusFour, Operator.SUBTRACT, constant(3)).evaluate(noVars), EPS);
            assertEquals(9.0, bin(constant(10), Operator.SUBTRACT, fourMinusThree).evaluate(noVars), EPS);
        }

        @Test
        @DisplayName("Ділення на нуль → ArithmeticException")
        void divisionByZero() {
            BinaryExpression expr = bin(constant(1), Operator.DIVIDE, constant(0));
            assertThrows(ArithmeticException.class, () -> expr.evaluate(noVars));
        }

        @Test
        @DisplayName("Ділення на -0.0 → ArithmeticException")
        void divisionByNegativeZero() {
            BinaryExpression expr = bin(constant(1), Operator.DIVIDE, constant(-0.0));
            assertThrows(ArithmeticException.class, () -> expr.evaluate(noVars));
        }

        @Test
        @DisplayName("Ділення на вираз, що дорівнює нулю: 1 / (x - x)")
        void divisionByComputedZero() {
            Expression zero = bin(variable("x"), Operator.SUBTRACT, variable("x"));
            BinaryExpression expr = bin(constant(1), Operator.DIVIDE, zero);
            assertThrows(ArithmeticException.class, () -> expr.evaluate(Map.of("x", 5.0)));
        }

        @Test
        @DisplayName("Поріг 1e-12: знаменник 1e-13 вважається нулем, 1e-11 ні")
        void divisionThreshold() {
            assertThrows(ArithmeticException.class,
                    () -> bin(constant(1), Operator.DIVIDE, constant(1e-13)).evaluate(noVars));
            assertThrows(ArithmeticException.class,
                    () -> bin(constant(1), Operator.DIVIDE, constant(-1e-13)).evaluate(noVars));
            assertEquals(1e11,
                    bin(constant(1), Operator.DIVIDE, constant(1e-11)).evaluate(noVars), 1.0);
        }

        @Test
        @DisplayName("Ділення нуля на нуль → ArithmeticException (а не NaN)")
        void zeroDividedByZero() {
            BinaryExpression expr = bin(constant(0), Operator.DIVIDE, constant(0));
            assertThrows(ArithmeticException.class, () -> expr.evaluate(noVars));
        }
    }


    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("Кожен оператор друкується зі своїм символом: a + b, a - b, a * b, a / b")
        void simpleOperators() {
            assertEquals(a + " + " + b, bin(a, Operator.ADD, b).toString());
            assertEquals(a + " - " + b, bin(a, Operator.SUBTRACT, b).toString());
            assertEquals(a + " * " + b, bin(a, Operator.MULTIPLY, b).toString());
            assertEquals(a + " / " + b, bin(a, Operator.DIVIDE, b).toString());
        }

        @Test
        @DisplayName("Нижчий пріоритет зліва: (a + b) * c")
        void lowerPrecedenceLeft() {
            Expression sum = bin(a, Operator.ADD, b);
            assertEquals("(" + sum + ") * " + c, bin(sum, Operator.MULTIPLY, c).toString());
        }

        @Test
        @DisplayName("Нижчий пріоритет справа: a * (b + c)")
        void lowerPrecedenceRight() {
            Expression sum = bin(b, Operator.ADD, c);
            assertEquals(a + " * (" + sum + ")", bin(a, Operator.MULTIPLY, sum).toString());
        }

        @Test
        @DisplayName("Вищий пріоритет дочірнього без дужок: a + b * c та a * b + c")
        void higherPrecedenceNoParens() {
            Expression product = bin(b, Operator.MULTIPLY, c);
            assertEquals(a + " + " + product, bin(a, Operator.ADD, product).toString());

            Expression leftProduct = bin(a, Operator.MULTIPLY, b);
            assertEquals(leftProduct + " + " + c, bin(leftProduct, Operator.ADD, c).toString());
        }

        @Test
        @DisplayName("Однаковий пріоритет зліва без дужок: (a - b) - c → a - b - c")
        void samePrecedenceLeftNoParens() {
            Expression diff = bin(a, Operator.SUBTRACT, b);
            assertEquals(diff + " - " + c, bin(diff, Operator.SUBTRACT, c).toString());

            Expression quotient = bin(a, Operator.DIVIDE, b);
            assertEquals(quotient + " / " + c, bin(quotient, Operator.DIVIDE, c).toString());
        }

        @Test
        @DisplayName("Правий операнд віднімання того самого пріоритету: a - (b - c), a - (b + c)")
        void rightChildOfSubtractionGetsParens() {
            Expression diff = bin(b, Operator.SUBTRACT, c);
            assertEquals(a + " - (" + diff + ")", bin(a, Operator.SUBTRACT, diff).toString());

            Expression sum = bin(b, Operator.ADD, c);
            assertEquals(a + " - (" + sum + ")", bin(a, Operator.SUBTRACT, sum).toString());
        }

        @Test
        @DisplayName("Правий операнд ділення того самого пріоритету: a / (b * c), a / (b / c)")
        void rightChildOfDivisionGetsParens() {
            Expression product = bin(b, Operator.MULTIPLY, c);
            assertEquals(a + " / (" + product + ")", bin(a, Operator.DIVIDE, product).toString());

            Expression quotient = bin(b, Operator.DIVIDE, c);
            assertEquals(a + " / (" + quotient + ")", bin(a, Operator.DIVIDE, quotient).toString());
        }

        @Test
        @DisplayName("Додавання і множення асоціативні: a + (b + c) → a + b + c, a * (b * c) → a * b * c")
        void associativeOperatorsNoParens() {
            Expression sum = bin(b, Operator.ADD, c);
            assertEquals(a + " + " + sum, bin(a, Operator.ADD, sum).toString());

            Expression product = bin(b, Operator.MULTIPLY, c);
            assertEquals(a + " * " + product, bin(a, Operator.MULTIPLY, product).toString());
        }

        @Test
        @DisplayName("a + (b - c): однаковий пріоритет, але оператор ADD, тому без дужок")
        void addWithRightSubtraction() {
            Expression diff = bin(b, Operator.SUBTRACT, c);
            assertEquals(a + " + " + diff, bin(a, Operator.ADD, diff).toString());
        }

        @Test
        @DisplayName("Дужки з обох боків: (a + b) / (c - d)")
        void parensOnBothSides() {
            Expression sum = bin(a, Operator.ADD, b);
            Expression diff = bin(c, Operator.SUBTRACT, d);
            assertEquals("(" + sum + ") / (" + diff + ")", bin(sum, Operator.DIVIDE, diff).toString());
        }

        @Test
        @DisplayName("Глибоке вкладення: a - (b - (c - d))")
        void deepNesting() {
            Expression inner = bin(c, Operator.SUBTRACT, d);
            Expression middle = bin(b, Operator.SUBTRACT, inner);
            BinaryExpression outer = bin(a, Operator.SUBTRACT, middle);
            assertEquals(a + " - (" + b + " - (" + c + " - " + d + "))", outer.toString());
        }

        @Test
        @DisplayName("Ліва частина віднімання з нижчим пріоритетом: (a - b) / c")
        void subtractionUnderDivision() {
            Expression diff = bin(a, Operator.SUBTRACT, b);
            assertEquals("(" + diff + ") / " + c, bin(diff, Operator.DIVIDE, c).toString());
        }
    }


    @Nested
    @DisplayName("equals / hashCode")
    class EqualityTests {

        @Test
        @DisplayName("Рефлексивність: вираз дорівнює сам собі")
        void reflexive() {
            BinaryExpression expr = bin(a, Operator.ADD, b);
            assertEquals(expr, expr);
        }

        @Test
        @DisplayName("Однакова структура → рівні, симетрично, з однаковим hashCode")
        void equalStructure() {
            BinaryExpression e1 = bin(a, Operator.ADD, b);
            BinaryExpression e2 = bin(a, Operator.ADD, b);
            assertEquals(e1, e2);
            assertEquals(e2, e1);
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        @DisplayName("Вкладені вирази з однаковою структурою рівні")
        void equalNested() {
            Expression sum = bin(a, Operator.ADD, b);
            assertEquals(bin(sum, Operator.MULTIPLY, c), bin(sum, Operator.MULTIPLY, c));
            assertEquals(bin(sum, Operator.MULTIPLY, c).hashCode(), bin(sum, Operator.MULTIPLY, c).hashCode());
        }

        @Test
        @DisplayName("Різний оператор → не рівні")
        void differentOperator() {
            assertNotEquals(bin(a, Operator.ADD, b), bin(a, Operator.SUBTRACT, b));
            assertNotEquals(bin(a, Operator.MULTIPLY, b), bin(a, Operator.DIVIDE, b));
        }

        @Test
        @DisplayName("Різні операнди → не рівні")
        void differentOperands() {
            assertNotEquals(bin(constant(1), Operator.ADD, b), bin(constant(2), Operator.ADD, b));
            assertNotEquals(bin(a, Operator.ADD, constant(1)), bin(a, Operator.ADD, constant(2)));
        }

        @Test
        @DisplayName("Порядок операндів важливий: 1 + 2 ≠ 2 + 1 (структурна рівність)")
        void operandOrderMatters() {
            Expression one = constant(1);
            Expression two = constant(2);
            assertNotEquals(bin(one, Operator.ADD, two), bin(two, Operator.ADD, one));
        }

        @Test
        @DisplayName("null та об'єкти інших класів → не рівні")
        void differentTypes() {
            BinaryExpression expr = bin(a, Operator.ADD, b);
            assertFalse(expr.equals(null));
            assertFalse(expr.equals("a + b"));
            assertFalse(expr.equals(a));
        }

        @Test
        @DisplayName("Працює як ключ у HashSet")
        void worksInHashSet() {
            Set<BinaryExpression> set = new HashSet<>();
            set.add(bin(a, Operator.ADD, b));
            assertTrue(set.contains(bin(a, Operator.ADD, b)));
            assertFalse(set.contains(bin(a, Operator.SUBTRACT, b)));
            set.add(bin(a, Operator.ADD, b));
            assertEquals(1, set.size());
        }
    }
}