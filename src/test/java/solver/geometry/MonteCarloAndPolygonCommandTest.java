package solver.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * CLI-команди monte-carlo-triangle і polygon (Lab 2).
 */
class MonteCarloAndPolygonCommandTest {

    private static final String INVALID = "Error: Invalid mathematical input";

    private static double parseResult(String out) {
        assertTrue(out.startsWith("Result: "), out);
        return Double.parseDouble(out.substring("Result: ".length()));
    }

    // ===================================================================
    // monte-carlo-triangle
    // ===================================================================

    @Test
    @DisplayName("monte-carlo-triangle: оцінка ймовірності близька до точного значення 1/2")
    void monteCarloConvergesToOneHalf() {
        // три незалежні U(0,1) утворюють трикутник з імовірністю рівно 1/2
        double p = parseResult(capture(() -> MonteCarlo.calculateMonteCarloTriangle(200_000)));
        assertEquals(0.5, p, 0.01);
    }

    @Test
    @DisplayName("monte-carlo-triangle: фіксований seed → відтворюваний результат")
    void monteCarloIsReproducible() {
        assertEquals(capture(() -> MonteCarlo.calculateMonteCarloTriangle(1000, 7L)),
                capture(() -> MonteCarlo.calculateMonteCarloTriangle(1000, 7L)));
        assertEquals(capture(() -> MonteCarlo.calculateMonteCarloTriangle(1000)),
                capture(() -> MonteCarlo.calculateMonteCarloTriangle(1000, 42L)));
    }

    @Test
    @DisplayName("monte-carlo-triangle: результат завжди в [0, 1]")
    void monteCarloIsProbability() {
        double p = parseResult(capture(() -> MonteCarlo.calculateMonteCarloTriangle(1)));
        assertTrue(p == 0.0 || p == 1.0, "одне випробування дає 0 або 1, а не " + p);
    }

    @Test
    @DisplayName("monte-carlo-triangle: n ≤ 0 → помилка")
    void monteCarloRejectsNonPositive() {
        assertEquals(INVALID, capture(() -> MonteCarlo.calculateMonteCarloTriangle(0)));
        assertEquals(INVALID, capture(() -> MonteCarlo.calculateMonteCarloTriangle(-5)));
    }

    // ===================================================================
    // polygon: периметр і опуклість
    // ===================================================================

    @Test
    @DisplayName("polygon: одиничний квадрат — периметр 4, опуклий")
    void polygonSquare() {
        assertEquals("Result: perimeter=4.000000, convex=true",
                capture(() -> Polygon.processPolygon(0, 0, 1, 0, 1, 1, 0, 1)));
    }

    @Test
    @DisplayName("polygon: обхід за годинниковою стрілкою теж опуклий")
    void polygonClockwise() {
        assertEquals("Result: perimeter=4.000000, convex=true",
                capture(() -> Polygon.processPolygon(0, 0, 0, 1, 1, 1, 1, 0)));
    }

    @Test
    @DisplayName("polygon: «стрілка» з увігнутою вершиною → convex=false")
    void polygonConcave() {
        // 0,0 → 2,0 → 1,1 → 2,2 → 0,2: периметр 2 + √2 + √2 + 2 + 2
        String expected = String.format(java.util.Locale.ROOT,
                "Result: perimeter=%f, convex=false", 6 + 2 * Math.sqrt(2));
        assertEquals(expected, capture(() -> Polygon.processPolygon(0, 0, 2, 0, 1, 1, 2, 2, 0, 2)));
    }

    @Test
    @DisplayName("polygon: вироджений (усі точки на прямій) не вважається опуклим")
    void polygonDegenerate() {
        String out = capture(() -> Polygon.processPolygon(0, 0, 1, 1, 2, 2));
        assertTrue(out.endsWith("convex=false"), out);
    }

    @Test
    @DisplayName("polygon: менше 3 вершин або непарна кількість координат → помилка")
    void polygonInvalidInput() {
        assertEquals(INVALID, capture(() -> Polygon.processPolygon(0, 0, 1, 1)));
        assertEquals(INVALID, capture(() -> Polygon.processPolygon(0, 0, 1, 0, 1, 1, 5)));
        assertEquals(INVALID, capture(() -> Polygon.processPolygon((double[]) null)));
    }
}
