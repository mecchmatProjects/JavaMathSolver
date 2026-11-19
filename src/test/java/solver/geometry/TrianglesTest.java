package solver.geometry;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * GEO-11, GEO-12, Lab 2 tasks 8-10: трикутник за сторонами (статичні CLI-методи).
 * Об'єктна модель {@link Triangle} перевіряється окремо в TriangleTest.
 */
class TrianglesTest {

    private static final String INVALID = "Error: Invalid mathematical input";
    private static final double EPS = 1e-12;

    private static String fmt(String pattern, Object... values) {
        return String.format(Locale.ROOT, pattern, values);
    }

    // ===================================================================
    // triangleValid / triangle-valid (GEO-12)
    // ===================================================================

    @Test
    @DisplayName("triangleValid: нерівність трикутника")
    void triangleValid() {
        assertTrue(Triangles.triangleValid(3, 4, 5));
        assertTrue(Triangles.triangleValid(1, 1, 1));
        assertFalse(Triangles.triangleValid(1, 2, 3));   // вироджений: a + b = c
        assertFalse(Triangles.triangleValid(1, 2, 10));
        assertFalse(Triangles.triangleValid(0, 1, 1));
        assertFalse(Triangles.triangleValid(-3, 4, 5));
    }

    @Test
    @DisplayName("triangle-valid: друкує true/false")
    void triangleValidCommand() {
        assertEquals("Result: true", capture(() -> Triangles.calculateTriangleValid(3, 4, 5)));
        assertEquals("Result: false", capture(() -> Triangles.calculateTriangleValid(1, 2, 3)));
    }

    // ===================================================================
    // heronArea / triangle-area (GEO-11)
    // ===================================================================

    @Test
    @DisplayName("heronArea: 3-4-5 → 6, рівносторонній → √3/4 a²")
    void heronArea() {
        assertEquals(6.0, Triangles.heronArea(3, 4, 5), EPS);
        assertEquals(Math.sqrt(3) / 4 * 4, Triangles.heronArea(2, 2, 2), EPS);
    }

    @Test
    @DisplayName("heronArea: вироджений або некоректний трикутник → 0")
    void heronAreaDegenerate() {
        assertEquals(0.0, Triangles.heronArea(1, 2, 3), EPS);
        assertEquals(0.0, Triangles.heronArea(0, 0, 0), EPS);
        assertEquals(0.0, Triangles.heronArea(1, 2, 10), EPS);
    }

    @Test
    @DisplayName("heronArea: нормування сторін не дає проміжного переповнення")
    void heronAreaLargeSides() {
        // наївна формула переповнюється вже на s·(s−a)·... ≈ 1e600; площа ж 6e300 — скінченна
        double s = 1e150;
        assertEquals(6.0, Triangles.heronArea(3 * s, 4 * s, 5 * s) / s / s, 1e-9);
    }

    @Test
    @DisplayName("triangle-area: якщо сама площа не вміщується в double → помилка")
    void triangleAreaOverflow() {
        assertEquals(INVALID, capture(() -> Triangles.calculateTriangleArea(3e200, 4e200, 5e200)));
    }

    @Test
    @DisplayName("triangle-area: результат і помилка для некоректних сторін")
    void triangleAreaCommand() {
        assertEquals("Result: 6.000000", capture(() -> Triangles.calculateTriangleArea(3, 4, 5)));
        assertEquals(INVALID, capture(() -> Triangles.calculateTriangleArea(1, 2, 3)));
        assertEquals(INVALID, capture(() -> Triangles.calculateTriangleArea(-3, 4, 5)));
    }

    // ===================================================================
    // Медіани, бісектриси, висоти (task 8)
    // ===================================================================

    @Test
    @DisplayName("triangle-medians: m_a = ½√(2b² + 2c² − a²)")
    void medians() {
        // 3-4-5: m_a = ½√73, m_b = ½√52, m_c = 2.5 (медіана до гіпотенузи = половина гіпотенузи)
        assertEquals(fmt("Result: m_a=%f, m_b=%f, m_c=%f", Math.sqrt(73) / 2, Math.sqrt(52) / 2, 2.5),
                capture(() -> Triangles.calculateMedians(3, 4, 5)));
        assertEquals(INVALID, capture(() -> Triangles.calculateMedians(1, 2, 3)));
    }

    @Test
    @DisplayName("triangle-bisectors: у рівносторонньому трикутнику бісектриса = висота = a√3/2")
    void bisectors() {
        double l = Math.sqrt(3);
        assertEquals(fmt("Result: l_a=%f, l_b=%f, l_c=%f", l, l, l),
                capture(() -> Triangles.calculateBisectors(2, 2, 2)));
        assertEquals(INVALID, capture(() -> Triangles.calculateBisectors(1, 2, 3)));
    }

    @Test
    @DisplayName("triangle-bisectors: 3-4-5, бісектриса прямого кута = √2·ab/(a+b)")
    void bisectorOfRightAngle() {
        String out = capture(() -> Triangles.calculateBisectors(3, 4, 5));
        assertTrue(out.endsWith(fmt("l_c=%f", Math.sqrt(2) * 12 / 7)), out);
    }

    @Test
    @DisplayName("triangle-heights: h = 2S / сторона")
    void heights() {
        // 3-4-5, S = 6: h_a = 4, h_b = 3, h_c = 2.4
        assertEquals("Result: h_a=4.000000, h_b=3.000000, h_c=2.400000",
                capture(() -> Triangles.calculateHeights(3, 4, 5)));
        assertEquals(INVALID, capture(() -> Triangles.calculateHeights(1, 2, 3)));
    }

    // ===================================================================
    // Площа за кутами і радіусом вписаного кола (task 9)
    // ===================================================================

    @Test
    @DisplayName("triangle-area-inradius: S = r²·Σ ctg(α/2)")
    void areaByAnglesAndInradius() {
        double third = Math.PI / 3;
        // рівносторонній, r = 1: 3·ctg(π/6) = 3√3
        assertEquals(fmt("Result: %f", 3 * Math.sqrt(3)),
                capture(() -> Triangles.calculateAreaByAnglesAndInradius(third, third, third, 1)));
        // r масштабує площу квадратично
        assertEquals(fmt("Result: %f", 4 * 3 * Math.sqrt(3)),
                capture(() -> Triangles.calculateAreaByAnglesAndInradius(third, third, third, 2)));
    }

    @Test
    @DisplayName("triangle-area-inradius: сума кутів ≠ π, кут ≤ 0 або ≥ π, r ≤ 0 → помилка")
    void areaByAnglesAndInradiusInvalid() {
        double third = Math.PI / 3;
        assertEquals(INVALID, capture(() -> Triangles.calculateAreaByAnglesAndInradius(1, 1, 1, 1)));
        assertEquals(INVALID, capture(() -> Triangles.calculateAreaByAnglesAndInradius(0, Math.PI / 2, Math.PI / 2, 1)));
        assertEquals(INVALID, capture(() -> Triangles.calculateAreaByAnglesAndInradius(Math.PI, 0.1, 0.1, 1)));
        assertEquals(INVALID, capture(() -> Triangles.calculateAreaByAnglesAndInradius(third, third, third, 0)));
        assertEquals(INVALID, capture(() -> Triangles.calculateAreaByAnglesAndInradius(third, third, third, -1)));
    }

    // ===================================================================
    // Кути (task 10)
    // ===================================================================

    @Test
    @DisplayName("triangle-angles: рівносторонній → 60° кожен")
    void anglesEquilateral() {
        double r = Math.PI / 3;
        assertEquals(fmt("Result: A=%f rad (%f deg), B=%f rad (%f deg), C=%f rad (%f deg)", r, 60.0, r, 60.0, r, 60.0),
                capture(() -> Triangles.calculateTriangleAngles(1, 1, 1)));
    }

    @Test
    @DisplayName("triangle-angles: 3-4-5 має прямий кут навпроти гіпотенузи")
    void anglesRightTriangle() {
        String out = capture(() -> Triangles.calculateTriangleAngles(3, 4, 5));
        assertTrue(out.endsWith(fmt("C=%f rad (%f deg)", Math.PI / 2, 90.0)), out);
    }

    @Test
    @DisplayName("triangle-angles: некоректні сторони → помилка")
    void anglesInvalid() {
        assertEquals(INVALID, capture(() -> Triangles.calculateTriangleAngles(1, 2, 3)));
    }
}
