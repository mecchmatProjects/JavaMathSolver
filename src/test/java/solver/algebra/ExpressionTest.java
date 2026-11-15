package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import solver.algebra.BinaryExpression.Operator;


class ExpressionTest {

    private static final double EPS = 1e-9;

    private static class RecordingExpression extends Expression {
        private final double result;
        private Map<String, Double> received;
        private int calls;

        RecordingExpression(double result) {
            this.result = result;
        }

        @Override
        public double evaluate(Map<String, Double> variables) {
            calls++;
            received = variables;
            return result;
        }

        @Override
        public String toString() {
            return "stub";
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static Expression constant(double value) {
        return new ConstantExpression(value);
    }

    private static Expression bin(Expression left, Operator op, Expression right) {
        return new BinaryExpression(left, op, right);
    }


    @Test
    @DisplayName("Expression є абстрактним класом")
    void classIsAbstract() {
        assertTrue(Modifier.isAbstract(Expression.class.getModifiers()));
    }

    @Test
    @DisplayName("Абстрактні: evaluate(Map), toString, equals, hashCode (підкласи зобов'язані їх перевизначити)")
    void contractMethodsAreAbstract() throws Exception {
        Method evaluate = Expression.class.getDeclaredMethod("evaluate", Map.class);
        Method toString = Expression.class.getDeclaredMethod("toString");
        Method equals = Expression.class.getDeclaredMethod("equals", Object.class);
        Method hashCode = Expression.class.getDeclaredMethod("hashCode");

        assertAll(
                () -> assertTrue(Modifier.isAbstract(evaluate.getModifiers()), "evaluate(Map)"),
                () -> assertTrue(Modifier.isAbstract(toString.getModifiers()), "toString"),
                () -> assertTrue(Modifier.isAbstract(equals.getModifiers()), "equals"),
                () -> assertTrue(Modifier.isAbstract(hashCode.getModifiers()), "hashCode")
        );
    }

    @Test
    @DisplayName("evaluate() без аргументів не абстрактний (готова реалізація в базовому класі)")
    void noArgEvaluateIsConcrete() throws Exception {
        Method evaluate = Expression.class.getDeclaredMethod("evaluate");
        assertFalse(Modifier.isAbstract(evaluate.getModifiers()));
    }


    @Test
    @DisplayName("evaluate() викликає evaluate(Map) один раз і повертає його результат")
    void noArgEvaluateDelegates() {
        RecordingExpression expr = new RecordingExpression(3.5);

        double result = expr.evaluate();

        assertEquals(3.5, result, EPS);
        assertEquals(1, expr.calls);
    }

    @Test
    @DisplayName("evaluate() передає порожню (не null) мапу змінних")
    void noArgEvaluatePassesEmptyMap() {
        RecordingExpression expr = new RecordingExpression(0);

        expr.evaluate();

        assertNotNull(expr.received);
        assertTrue(expr.received.isEmpty());
    }

    @Test
    @DisplayName("Мапа, яку передає evaluate(), незмінна")
    void noArgEvaluatePassesUnmodifiableMap() {
        RecordingExpression expr = new RecordingExpression(0);

        expr.evaluate();

        assertThrows(UnsupportedOperationException.class, () -> expr.received.put("x", 1.0));
    }

    @Test
    @DisplayName("evaluate() повертає результат як є, зокрема від'ємний і NaN")
    void noArgEvaluateReturnsResultUnchanged() {
        assertEquals(-2.0, new RecordingExpression(-2).evaluate(), EPS);
        assertTrue(Double.isNaN(new RecordingExpression(Double.NaN).evaluate()));
    }


    @Test
    @DisplayName("Підкласи можна використовувати як Expression")
    void subclassesAreExpressions() {
        assertInstanceOf(Expression.class, constant(1));
        assertInstanceOf(Expression.class, bin(constant(1), Operator.ADD, constant(2)));
    }

    @Test
    @DisplayName("evaluate() для константи: 7 → 7")
    void evaluateConstantNoArg() {
        assertEquals(7.0, constant(7).evaluate(), EPS);
    }

    @Test
    @DisplayName("evaluate() для виразу з констант: 2 + 3 = 5, (1 + 2) * 4 = 12")
    void evaluateBinaryNoArg() {
        Expression sum = bin(constant(2), Operator.ADD, constant(3));
        assertEquals(5.0, sum.evaluate(), EPS);

        Expression nested = bin(bin(constant(1), Operator.ADD, constant(2)), Operator.MULTIPLY, constant(4));
        assertEquals(12.0, nested.evaluate(), EPS);
    }

    @Test
    @DisplayName("evaluate() і evaluate(Map.of()) дають однаковий результат")
    void noArgMatchesEmptyMap() {
        Expression expr = bin(bin(constant(10), Operator.SUBTRACT, constant(4)), Operator.DIVIDE, constant(3));
        assertEquals(expr.evaluate(Map.of()), expr.evaluate(), EPS);
        assertEquals(2.0, expr.evaluate(), EPS);
    }

    @Test
    @DisplayName("evaluate(): помилка обчислення (ділення на нуль) долітає до викликача")
    void evaluateErrorPropagates() {
        Expression expr = bin(constant(1), Operator.DIVIDE, constant(0));
        assertThrows(ArithmeticException.class, expr::evaluate);
    }

    @Test
    @DisplayName("Поліморфізм: список різних виразів обчислюється через базовий тип")
    void polymorphicEvaluation() {
        List<Expression> expressions = List.of(
                constant(4),
                bin(constant(2), Operator.MULTIPLY, constant(3)),
                bin(constant(9), Operator.SUBTRACT, constant(1))
        );

        double sum = 0;
        for (Expression e : expressions) {
            sum += e.evaluate();
        }

        assertEquals(18.0, sum, EPS); // 4 + 6 + 8
    }


    @Test
    @DisplayName("toString: поліморфний виклик, константи без «.0», дужки за пріоритетом")
    void polymorphicToString() {
        assertEquals("5", constant(5).toString());
        assertEquals("2 + 3", bin(constant(2), Operator.ADD, constant(3)).toString());
        assertEquals("(1 + 2) * 4",
                bin(bin(constant(1), Operator.ADD, constant(2)), Operator.MULTIPLY, constant(4)).toString());
    }

    @Test
    @DisplayName("equals/hashCode: однакові за структурою вирази рівні й мають однаковий хеш")
    void equalExpressionsHaveEqualHashCodes() {
        Expression c1 = constant(2.5);
        Expression c2 = constant(2.5);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        Expression b1 = bin(bin(constant(1), Operator.ADD, constant(2)), Operator.MULTIPLY, constant(3));
        Expression b2 = bin(bin(constant(1), Operator.ADD, constant(2)), Operator.MULTIPLY, constant(3));
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    @DisplayName("equals: різні за структурою вирази не рівні")
    void differentExpressionsAreNotEqual() {
        Expression sum = bin(constant(1), Operator.ADD, constant(2));
        Expression product = bin(constant(1), Operator.MULTIPLY, constant(2));
        Expression swapped = bin(constant(2), Operator.ADD, constant(1));

        assertNotEquals(sum, product);
        assertNotEquals(sum, swapped);
    }

    @Test
    @DisplayName("equals: константа й бінарний вираз з тим самим значенням не рівні")
    void differentTypesAreNotEqual() {
        Expression c = constant(3);
        Expression b = bin(constant(1), Operator.ADD, constant(2));

        assertEquals(c.evaluate(), b.evaluate(), EPS);
        assertNotEquals(c, b);
        assertNotEquals(b, c);
    }

    @Test
    @DisplayName("HashSet<Expression>: однакові вирази зливаються, різні типи співіснують")
    void hashSetOfMixedExpressions() {
        Set<Expression> set = new HashSet<>();
        set.add(constant(1));
        set.add(constant(1));
        set.add(bin(constant(1), Operator.ADD, constant(2)));
        set.add(bin(constant(1), Operator.ADD, constant(2)));
        set.add(constant(3));

        assertEquals(3, set.size());
        assertTrue(set.contains(bin(constant(1), Operator.ADD, constant(2))));
    }
}