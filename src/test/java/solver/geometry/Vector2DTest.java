package solver.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2DTest {

    private static final double EPS = 1e-9;

    private static void assertVec(double x, double y, Vector2D v) {
        assertEquals(x, v.x, EPS, "x");
        assertEquals(y, v.y, EPS, "y");
    }

    @Test
    void betweenPoints() {
        assertVec(2, 3, Vector2D.between(Point.of(1, 1), Point.of(3, 4)));
    }

    @Test
    void addSubtractScaleNegate() {
        Vector2D a = Vector2D.of(1, 2);
        Vector2D b = Vector2D.of(3, -1);
        assertVec(4, 1, a.add(b));
        assertVec(-2, 3, a.subtract(b));
        assertVec(2.5, 5, a.scale(2.5));
        assertVec(-1, -2, a.negate());
    }

    @Test
    void dotAndCross() {
        Vector2D a = Vector2D.of(1, 2);
        Vector2D b = Vector2D.of(3, 4);
        assertEquals(11.0, a.dot(b), EPS);
        assertEquals(-2.0, a.cross(b), EPS);
        assertEquals(2.0, b.cross(a), EPS);
    }

    @Test
    void lengthAndLengthSquared() {
        Vector2D v = Vector2D.of(3, 4);
        assertEquals(5.0, v.length(), EPS);
        assertEquals(25.0, v.lengthSquared(), EPS);
        assertEquals(0.0, Vector2D.ZERO.length());
    }

    @Test
    void normalizeGivesUnitVector() {
        Vector2D n = Vector2D.of(3, 4).normalize();
        assertVec(0.6, 0.8, n);
        assertEquals(1.0, n.length(), EPS);
    }

    @Test
    void normalizeZeroThrows() {
        assertThrows(ArithmeticException.class, () -> Vector2D.ZERO.normalize());
    }

    @Test
    void perpendicularIsOrthogonal() {
        Vector2D v = Vector2D.of(2, 5);
        Vector2D p = v.perpendicular();
        assertVec(-5, 2, p);
        assertEquals(0.0, v.dot(p), EPS);
    }

    @Test
    void rotateByRightAngle() {
        assertVec(0, 1, Vector2D.of(1, 0).rotate(Math.PI / 2));
        assertVec(-1, 0, Vector2D.of(1, 0).rotate(Math.PI));
    }

    @Test
    void angleTo() {
        assertEquals(Math.PI / 2, Vector2D.of(1, 0).angleTo(Vector2D.of(0, 5)), EPS);
        assertEquals(Math.PI, Vector2D.of(1, 1).angleTo(Vector2D.of(-2, -2)), EPS);
        assertEquals(0.0, Vector2D.of(2, 2).angleTo(Vector2D.of(1, 1)), EPS);
        assertEquals(Math.PI / 4, Vector2D.of(1, 0).angleTo(Vector2D.of(1, 1)), EPS);
    }

    @Test
    void angleWithZeroVectorThrows() {
        assertThrows(ArithmeticException.class, () -> Vector2D.of(1, 0).angleTo(Vector2D.ZERO));
    }

    @Test
    void parallelCheck() {
        assertTrue(Vector2D.of(1, 2).isParallelTo(Vector2D.of(-3, -6), EPS));
        assertFalse(Vector2D.of(1, 2).isParallelTo(Vector2D.of(2, 1), EPS));
    }

    @Test
    void operationsDoNotMutate() {
        Vector2D v = Vector2D.of(1, 2);
        v.add(Vector2D.of(5, 5));
        v.scale(10);
        v.rotate(1);
        assertVec(1, 2, v);
    }

    @Test
    void vectorsWithSameComponentsAreEqual() {
        Vector2D a = Vector2D.of(3, 4);
        Vector2D b = Vector2D.between(Point.of(0, 0), Point.of(3, 4));
        assertEquals(a, b);
    }

    @Test
    void vectorsWithDifferentComponentsAreNotEqual() {
        assertNotEquals(Vector2D.of(3, 4), Vector2D.of(4, 3));
        assertNotEquals(Vector2D.of(3, 4), Vector2D.of(3, 4).negate());
    }

    @Test
    void vectorIsNotEqualToNullOrPoint() {
        Vector2D v = Vector2D.of(1, 2);
        assertFalse(v.equals(null));
        assertFalse(v.equals(Point.of(1, 2)));
    }

    @Test
    void zeroConstantEqualsNewZeroVector() {
        assertEquals(Vector2D.ZERO, Vector2D.of(0, 0));
        assertEquals(Vector2D.ZERO, Vector2D.of(5, 5).subtract(Vector2D.of(5, 5)));
    }

    @Test
    void toStringShowsFields() {
        assertEquals("Vector2D(3.0, -4.0)", Vector2D.of(3, -4).toString());
    }
}