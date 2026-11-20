package solver.algebra;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.Output.printResult;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class EquationsTest {



    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void assertPrintsNumber(double expected, Runnable action) {
        assertEquals(capture(() -> printResult(expected)), capture(action));
    }

    private static void assertPrintsText(String expected, Runnable action) {
        assertEquals(capture(() -> printResult(expected)), capture(action));
    }


    @Test
    @DisplayName("solveLinear: 2x - 4 = 0 → x = 2")
    void linearBasic() {
        assertPrintsNumber(2.0, () -> Equations.solveLinear(2, -4));
    }

    @Test
    @DisplayName("solveLinear: -3x + 6 = 0 → x = 2")
    void linearNegativeCoefficient() {
        assertPrintsNumber(2.0, () -> Equations.solveLinear(-3, 6));
    }

    @Test
    @DisplayName("solveLinear: 4x + 2 = 0 → x = -0.5")
    void linearFractionalRoot() {
        assertPrintsNumber(-0.5, () -> Equations.solveLinear(4, 2));
    }

    @Test
    @DisplayName("solveLinear: 3x + 1 = 0 → x = -1/3")
    void linearNonTerminatingRoot() {
        assertPrintsNumber(-1.0 / 3.0, () -> Equations.solveLinear(3, 1));
    }

    @Test
    @DisplayName("solveLinear: 0x + 0 = 0 → нескінченно багато розв'язків")
    void linearInfiniteSolutions() {
        assertPrintsText("Infinite solutions", () -> Equations.solveLinear(0, 0));
    }

    @Test
    @DisplayName("solveLinear: 0x + 5 = 0 → розв'язків немає")
    void linearNoSolution() {
        assertPrintsText("No solution", () -> Equations.solveLinear(0, 5));
        assertPrintsText("No solution", () -> Equations.solveLinear(0, -5));
    }


    @Test
    @DisplayName("solveQuadratic: x² - 3x + 2 = 0 → 1 і 2")
    void quadraticTwoRootsBNegative() {
        assertPrintsText("x1=1.000000, x2=2.000000", () -> Equations.solveQuadratic(1, -3, 2));
    }

    @Test
    @DisplayName("solveQuadratic: x² + 3x + 2 = 0 → -2 і -1 (гілка b ≥ 0)")
    void quadraticTwoRootsBPositive() {
        assertPrintsText("x1=-2.000000, x2=-1.000000", () -> Equations.solveQuadratic(1, 3, 2));
    }

    @Test
    @DisplayName("solveQuadratic: x² - 2x - 3 = 0 → -1 і 3")
    void quadraticRootsDifferentSigns() {
        assertPrintsText("x1=-1.000000, x2=3.000000", () -> Equations.solveQuadratic(1, -2, -3));
    }

    @Test
    @DisplayName("solveQuadratic: -x² + 4 = 0 → -2 і 2 (a < 0, b = 0)")
    void quadraticNegativeLeadingCoefficient() {
        assertPrintsText("x1=-2.000000, x2=2.000000", () -> Equations.solveQuadratic(-1, 0, 4));
    }

    @Test
    @DisplayName("solveQuadratic: x1 завжди менший за x2, незалежно від знака a")
    void quadraticRootsAreSorted() {
        // 2x² - 4x - 6 = 0 → корені -1 і 3; -2x² + 4x + 6 = 0 → ті самі корені
        assertPrintsText("x1=-1.000000, x2=3.000000", () -> Equations.solveQuadratic(2, -4, -6));
        assertPrintsText("x1=-1.000000, x2=3.000000", () -> Equations.solveQuadratic(-2, 4, 6));
    }

    @Test
    @DisplayName("solveQuadratic: x² - 5x + 6 = 0 → 2 і 3 (коефіцієнти не одиничні: 2x² - 10x + 12)")
    void quadraticScaledCoefficients() {
        assertPrintsText("x1=2.000000, x2=3.000000", () -> Equations.solveQuadratic(2, -10, 12));
    }


    @Test
    @DisplayName("solveQuadratic: x² - 2x + 1 = 0 → один корінь x = 1")
    void quadraticDoubleRootPositive() {
        assertPrintsNumber(1.0, () -> Equations.solveQuadratic(1, -2, 1));
    }

    @Test
    @DisplayName("solveQuadratic: x² + 2x + 1 = 0 → один корінь x = -1")
    void quadraticDoubleRootNegative() {
        assertPrintsNumber(-1.0, () -> Equations.solveQuadratic(1, 2, 1));
    }

    @Test
    @DisplayName("solveQuadratic: 4x² - 12x + 9 = 0 → x = 1.5")
    void quadraticDoubleRootFractional() {
        assertPrintsNumber(1.5, () -> Equations.solveQuadratic(4, -12, 9));
    }


    @Test
    @DisplayName("solveQuadratic: x² + 1 = 0 → дійсних коренів немає")
    void quadraticNoRealRoots() {
        assertPrintsText("No real roots", () -> Equations.solveQuadratic(1, 0, 1));
    }

    @Test
    @DisplayName("solveQuadratic: x² + x + 1 = 0 → дійсних коренів немає (d = -3)")
    void quadraticNoRealRootsWithLinearTerm() {
        assertPrintsText("No real roots", () -> Equations.solveQuadratic(1, 1, 1));
    }


    @Test
    @DisplayName("solveQuadratic: a = 0 → те саме, що solveLinear(b, c)")
    void quadraticDegeneratesToLinear() {
        assertEquals(capture(() -> Equations.solveLinear(2, -4)),
                capture(() -> Equations.solveQuadratic(0, 2, -4)));
        assertPrintsNumber(2.0, () -> Equations.solveQuadratic(0, 2, -4));
    }

    @Test
    @DisplayName("solveQuadratic: a = 0, b = 0 → нескінченно багато розв'язків або жодного")
    void quadraticDegeneratesToConstant() {
        assertPrintsText("Infinite solutions", () -> Equations.solveQuadratic(0, 0, 0));
        assertPrintsText("No solution", () -> Equations.solveQuadratic(0, 0, 3));
    }


    @Test
    @DisplayName("Корінь x = 0 не має друкуватися як -0.0")
    void zeroRootIsNotNegativeZero() {
        assertAll(
                () -> assertPrintsNumber(0.0, () -> Equations.solveLinear(5, 0)),
                () -> assertPrintsNumber(0.0, () -> Equations.solveQuadratic(1, 0, 0))
        );
    }


    @Test
    @DisplayName("Утиліт-клас: приватний конструктор")
    void privateConstructor() throws Exception {
        var ctor = Equations.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(ctor.getModifiers()));
    }
}