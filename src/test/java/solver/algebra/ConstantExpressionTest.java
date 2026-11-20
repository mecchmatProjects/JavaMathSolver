package solver.algebra;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantExpressionTest {

    private static final double DELTA = 1e-9;

    @Test
    void testEvaluateWithoutVariables() {
        ConstantExpression expr = new ConstantExpression(3.14);

        // Використовуємо метод evaluate() без аргументів з базового класу Expression
        assertEquals(3.14, expr.evaluate(), DELTA, "Константа має повертати своє значення");
    }


    @Test
    void testEvaluateWithVariables() {
        ConstantExpression expr = new ConstantExpression(5.0);
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 10.0);
        variables.put("y", -2.5);

        // Константа має ігнорувати будь-які змінні і повертати своє число
        assertEquals(5.0, expr.evaluate(variables), DELTA, "Константа не повинна залежати від змінних");
    }

    @Test
    void testToStringFormatting() {
        ConstantExpression integerExpr = new ConstantExpression(5.0);
        ConstantExpression negativeIntegerExpr = new ConstantExpression(-7.0);
        ConstantExpression decimalExpr = new ConstantExpression(3.14);

        // Перевіряємо, чи правильно відкидається ".0" для цілих чисел
        assertEquals("5", integerExpr.toString());
        assertEquals("-7", negativeIntegerExpr.toString());

        // Дробові числа мають виводитися як є
        assertEquals("3.14", decimalExpr.toString());
    }

    @Test
    void testEquals() {
        ConstantExpression a = new ConstantExpression(5.0);
        ConstantExpression b = new ConstantExpression(5.0);
        ConstantExpression c = new ConstantExpression(-7.0);

        assertEquals(a, b, "Константи з однаковим значенням мають бути рівними");
        assertNotEquals(a, c, "Константи з різними значеннями не рівні");
        assertNotEquals(null, a, "Константа не дорівнює null");
        assertNotEquals(new Object(), a, "Константа не дорівнює об'єкту іншого класу");
    }

    @Test
    void testHashCode() {
        ConstantExpression a = new ConstantExpression(3.14);
        ConstantExpression b = new ConstantExpression(3.14);

        assertEquals(a.hashCode(), b.hashCode(), "Однакові константи повинні мати однаковий hashCode");
    }



}
