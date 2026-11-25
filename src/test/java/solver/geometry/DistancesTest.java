package solver.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * GEO-01, GEO-06, GEO-13 ... GEO-16: distance, origin-distance, manhattan-distance,
 * midpoint, collinear, quadrant.
 */
class DistancesTest {

    // ===================================================================
    // distance (GEO-01)
    // ===================================================================

    @Test
    @DisplayName("distance: єгипетський трикутник 3-4-5")
    void distanceClassicExample() {
        assertEquals("Result: 5.000000", capture(() -> Distances.calculateDistance(1, 2, 4, 6)));
    }

    @Test
    @DisplayName("distance: та сама точка → 0, порядок точок не важливий")
    void distanceSymmetricAndZero() {
        assertEquals("Result: 0.000000", capture(() -> Distances.calculateDistance(3, -7, 3, -7)));
        assertEquals(capture(() -> Distances.calculateDistance(1, 2, 4, 6)),
                capture(() -> Distances.calculateDistance(4, 6, 1, 2)));
    }

    @Test
    @DisplayName("distance: від'ємні та дробові координати")
    void distanceNegativeAndFractional() {
        assertEquals("Result: 1.414214", capture(() -> Distances.calculateDistance(-0.5, -0.5, 0.5, 0.5)));
    }

    @Test
    @DisplayName("distance: переповнення → Invalid mathematical input, а не Infinity")
    void distanceOverflowIsReported() {
        assertEquals("Error: Invalid mathematical input",
                capture(() -> Distances.calculateDistance(-1e300, 0, 1e300, 0)));
    }

    // ===================================================================
    // origin-distance (GEO-06)
    // ===================================================================

    @Test
    @DisplayName("origin-distance: (3, 4) → 5, (0, 0) → 0, знак координат не впливає")
    void originDistance() {
        assertEquals("Result: 5.000000", capture(() -> Distances.calculateOriginDistance(3, 4)));
        assertEquals("Result: 5.000000", capture(() -> Distances.calculateOriginDistance(-3, -4)));
        assertEquals("Result: 0.000000", capture(() -> Distances.calculateOriginDistance(0, 0)));
    }

    // ===================================================================
    // manhattan-distance (GEO-14)
    // ===================================================================

    @Test
    @DisplayName("manhattan-distance: |dx| + |dy|")
    void manhattanDistance() {
        assertEquals("Result: 7.000000", capture(() -> Distances.calculateManhattanDistance(1, 2, 4, 6)));
        assertEquals("Result: 7.000000", capture(() -> Distances.calculateManhattanDistance(4, 6, 1, 2)));
        assertEquals("Result: 0.000000", capture(() -> Distances.calculateManhattanDistance(2, 2, 2, 2)));
    }

    @Test
    @DisplayName("manhattan-distance ≥ евклідова відстань")
    void manhattanIsNotLessThanEuclidean() {
        // (0,0)-(1,1): манхеттенська 2, евклідова √2
        assertEquals("Result: 2.000000", capture(() -> Distances.calculateManhattanDistance(0, 0, 1, 1)));
    }

    // ===================================================================
    // midpoint (GEO-15)
    // ===================================================================

    @Test
    @DisplayName("midpoint: середина відрізка у форматі (x, y)")
    void midpoint() {
        assertEquals("Result: (2.500000, 4.000000)", capture(() -> Distances.calculateMidpoint(1, 2, 4, 6)));
        assertEquals("Result: (0.000000, 0.000000)", capture(() -> Distances.calculateMidpoint(-1, -1, 1, 1)));
    }

    // ===================================================================
    // collinear (GEO-16)
    // ===================================================================

    @Test
    @DisplayName("collinear: точки на прямій y = x")
    void collinearTrue() {
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(0, 0, 1, 1, 2, 2)));
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(0, 5, 1, 5, -7, 5))); // горизонталь
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(3, 0, 3, 1, 3, 9)));  // вертикаль
    }

    @Test
    @DisplayName("collinear: вершини трикутника → false")
    void collinearFalse() {
        assertEquals("Result: false", capture(() -> Distances.calculateCollinear(0, 0, 1, 0, 0, 1)));
    }

    @Test
    @DisplayName("collinear: збіжні точки завжди лежать на одній прямій")
    void collinearCoincidentPoints() {
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(1, 1, 1, 1, 5, 7)));
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(2, 2, 2, 2, 2, 2)));
    }

    @Test
    @DisplayName("collinear: допуск масштабується з довжиною (великі координати)")
    void collinearLargeCoordinates() {
        assertEquals("Result: true", capture(() -> Distances.calculateCollinear(0, 0, 1e8, 1e8, 2e8, 2e8)));
        assertEquals("Result: false", capture(() -> Distances.calculateCollinear(0, 0, 1e8, 0, 0, 1e8)));
    }

    // ===================================================================
    // quadrant (GEO-13)
    // ===================================================================

    @Test
    @DisplayName("quadrant: усі чотири чверті")
    void quadrants() {
        assertEquals("Result: I", capture(() -> Distances.calculateQuadrant(1, 1)));
        assertEquals("Result: II", capture(() -> Distances.calculateQuadrant(-1, 1)));
        assertEquals("Result: III", capture(() -> Distances.calculateQuadrant(-1, -1)));
        assertEquals("Result: IV", capture(() -> Distances.calculateQuadrant(1, -1)));
    }

    @Test
    @DisplayName("quadrant: точки на осях і початок координат")
    void quadrantAxesAndOrigin() {
        assertEquals("Result: ORIGIN", capture(() -> Distances.calculateQuadrant(0, 0)));
        assertEquals("Result: AXIS", capture(() -> Distances.calculateQuadrant(0, 5)));
        assertEquals("Result: AXIS", capture(() -> Distances.calculateQuadrant(-5, 0)));
    }

    @Test
    @DisplayName("quadrant: -0.0 вважається нулем")
    void quadrantNegativeZero() {
        assertEquals("Result: ORIGIN", capture(() -> Distances.calculateQuadrant(-0.0, -0.0)));
        assertEquals("Result: AXIS", capture(() -> Distances.calculateQuadrant(-0.0, 3)));
    }
}
