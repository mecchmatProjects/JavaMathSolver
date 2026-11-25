package solver.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Спільні геометричні примітиви: clamp, cross, orient, onSegment, segmentsIntersect.
 * Клас package-private, тому тест лежить у тому ж пакеті.
 */
class GeoMathTest {

    private static Point p(double x, double y) {
        return Point.of(x, y);
    }

    @Test
    @DisplayName("clamp: обрізає до [-1, 1]")
    void clamp() {
        assertEquals(1.0, GeoMath.clamp(1.0000001));
        assertEquals(-1.0, GeoMath.clamp(-5));
        assertEquals(0.3, GeoMath.clamp(0.3));
    }

    @Test
    @DisplayName("cross: знак визначає поворот, модуль — подвоєна площа")
    void cross() {
        assertEquals(1.0, GeoMath.cross(p(0, 0), p(1, 0), p(0, 1)));
        assertEquals(-1.0, GeoMath.cross(p(0, 0), p(0, 1), p(1, 0)));
        assertEquals(0.0, GeoMath.cross(p(0, 0), p(1, 1), p(2, 2)));
    }

    @Test
    @DisplayName("orient: ліворуч 1, праворуч -1, на прямій 0")
    void orient() {
        assertEquals(1, GeoMath.orient(p(0, 0), p(1, 0), p(0, 1)));
        assertEquals(-1, GeoMath.orient(p(0, 0), p(1, 0), p(0, -1)));
        assertEquals(0, GeoMath.orient(p(0, 0), p(1, 0), p(5, 0)));
    }

    @Test
    @DisplayName("orient: допуск відносний — мізерне відхилення на великих координатах вважається нулем")
    void orientRelativeTolerance() {
        assertEquals(0, GeoMath.orient(p(0, 0), p(1e9, 0), p(2e9, 1e-3)));
        assertEquals(1, GeoMath.orient(p(0, 0), p(1e-6, 0), p(0, 1e-6)));
    }

    @Test
    @DisplayName("onSegment: усередині, на кінці, за межами, поза прямою")
    void onSegment() {
        assertTrue(GeoMath.onSegment(p(0, 0), p(2, 2), p(1, 1)));
        assertTrue(GeoMath.onSegment(p(0, 0), p(2, 2), p(2, 2)));
        assertFalse(GeoMath.onSegment(p(0, 0), p(2, 2), p(3, 3)));
        assertFalse(GeoMath.onSegment(p(0, 0), p(2, 2), p(1, 0)));
    }

    @Test
    @DisplayName("segmentsIntersect: схрещення, дотик кінцем, паралельні, колінеарні з перекриттям")
    void segmentsIntersect() {
        assertTrue(GeoMath.segmentsIntersect(p(0, 0), p(2, 2), p(0, 2), p(2, 0)));
        assertTrue(GeoMath.segmentsIntersect(p(0, 0), p(1, 1), p(1, 1), p(2, 0)));
        assertFalse(GeoMath.segmentsIntersect(p(0, 0), p(1, 0), p(0, 1), p(1, 1)));
        assertTrue(GeoMath.segmentsIntersect(p(0, 0), p(2, 0), p(1, 0), p(3, 0)));
        assertFalse(GeoMath.segmentsIntersect(p(0, 0), p(1, 0), p(2, 0), p(3, 0)));
    }
}
