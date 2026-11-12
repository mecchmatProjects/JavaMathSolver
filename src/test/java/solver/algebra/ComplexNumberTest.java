package solver.algebra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComplexNumberTest {

    private static final double DELTA = 1e-9;

    @Test
    void testGetters() {
        ComplexNumber a = new ComplexNumber(3.5, -2.1);

        assertEquals(3.5, a.getReal(), DELTA, "Дійсна частина має збігатися");
        assertEquals(-2.1, a.getImaginary(), DELTA, "Уявна частина має збігатися");
    }

    @Test
    void testAdd() {
        ComplexNumber a = new ComplexNumber(1, 2); // 1 + 2i
        ComplexNumber b = new ComplexNumber(3, 4); // 3 + 4i
        ComplexNumber result = a.add(b);           // 4 + 6i

        assertEquals(4.0, result.getReal(), DELTA);
        assertEquals(6.0, result.getImaginary(), DELTA);
    }

    @Test
    void testSubtract() {
        ComplexNumber a = new ComplexNumber(5, 5); // 5 + 5i
        ComplexNumber b = new ComplexNumber(2, 8); // 2 + 8i
        ComplexNumber result = a.subtract(b);      // 3 - 3i

        assertEquals(3.0, result.getReal(), DELTA);
        assertEquals(-3.0, result.getImaginary(), DELTA);
    }

    @Test
    void testMultiply() {
        ComplexNumber a = new ComplexNumber(1, 2); // 1 + 2i
        ComplexNumber b = new ComplexNumber(3, 4); // 3 + 4i
        ComplexNumber result = a.multiply(b);      // (1*3 - 2*4) + (1*4 + 2*3)i = -5 + 10i

        assertEquals(-5.0, result.getReal(), DELTA);
        assertEquals(10.0, result.getImaginary(), DELTA);
    }

    @Test
    void testAbs() {
        ComplexNumber a = new ComplexNumber(3, 4); // Модуль: sqrt(3^2 + 4^2) = sqrt(25) = 5
        ComplexNumber b = new ComplexNumber(-5, 12); // Модуль: sqrt(25 + 144) = sqrt(169) = 13

        assertEquals(5.0, a.abs(), DELTA);
        assertEquals(13.0, b.abs(), DELTA);
    }

    @Test
    void testMultiplyByZero() {
        ComplexNumber a = new ComplexNumber(5, -7);
        ComplexNumber zero = new ComplexNumber(0, 0);
        ComplexNumber result = a.multiply(zero);

        assertEquals(0.0, result.getReal(), DELTA);
        assertEquals(0.0, result.getImaginary(), DELTA);
    }

    @Test
    void testDivide() {
        ComplexNumber a = new ComplexNumber(1, 2); // 1 + 2i
        ComplexNumber b = new ComplexNumber(3, 4); // 3 + 4i

        // Знаменник: 3^2 + 4^2 = 25
        // Дійсна частина: (1*3 + 2*4) / 25 = 11 / 25 = 0.44
        // Уявна частина: (2*3 - 1*4) / 25 = 2 / 25 = 0.08
        ComplexNumber result = a.divide(b);

        assertEquals(0.44, result.getReal(), DELTA, "Дійсна частина при діленні");
        assertEquals(0.08, result.getImaginary(), DELTA, "Уявна частина при діленні");
    }

    @Test
    void testDivideByZero() {
        ComplexNumber a = new ComplexNumber(5, -7);
        ComplexNumber zero = new ComplexNumber(0, 0);

        assertThrows(
                ArithmeticException.class,
                () -> a.divide(zero),
                "Ділення на 0 + 0i має викидати ArithmeticException"
        );
    }


}
