package solver.geometry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * Lab 2 tasks 14-18: circle-segment, circle-line, circles-intersect,
 * squares-intersect, rect-bounding-box.
 */
class IntersectionsTest {

    private static final String INVALID = "Error: Invalid mathematical input";

    // ===================================================================
    // circle-segment (task 14): коло x² + y² = r² і вертикальний відрізок на прямій x
    // Кейси підібрані так, щоб не залежати від того, як len задає кінець відрізка.
    // ===================================================================

    @Test
    @DisplayName("circle-segment: пряма поза колом → 0 точок")
    void circleSegmentLineOutside() {
        assertEquals("Result: 0", capture(() -> Intersections.calculateCircleSegmentIntersections(5, 6, -100, 1)));
    }

    @Test
    @DisplayName("circle-segment: відрізок охоплює одну з двох точок перетину")
    void circleSegmentOnePoint() {
        // r = 5, x = 3 → y = ±4; відрізок [-4, -3] містить лише -4
        assertEquals("Result: 1", capture(() -> Intersections.calculateCircleSegmentIntersections(5, 3, -4, 1)));
        // відрізок нульової довжини рівно в точці (3, 4)
        assertEquals("Result: 1", capture(() -> Intersections.calculateCircleSegmentIntersections(5, 3, 4, 0)));
    }

    @Test
    @DisplayName("circle-segment: відрізок не дотягується до кола → 0")
    void circleSegmentMisses() {
        // y = ±4, відрізок [0, 1]
        assertEquals("Result: 0", capture(() -> Intersections.calculateCircleSegmentIntersections(5, 3, 0, 1)));
    }

    @Test
    @DisplayName("circle-segment: дотична x = r → 1 точка, якщо y = 0 на відрізку")
    void circleSegmentTangent() {
        assertEquals("Result: 1", capture(() -> Intersections.calculateCircleSegmentIntersections(3, 3, -1, 1)));
        assertEquals("Result: 0", capture(() -> Intersections.calculateCircleSegmentIntersections(3, 3, 1, 1)));
    }

    @Test
    @DisplayName("circle-segment: від'ємний радіус → помилка")
    void circleSegmentNegativeRadius() {
        assertEquals(INVALID, capture(() -> Intersections.calculateCircleSegmentIntersections(-1, 0, 0, 1)));
    }

    // ===================================================================
    // circle-line (task 15)
    // ===================================================================

    @Test
    @DisplayName("circle-line: перетин, дотик, немає спільних точок")
    void circleLine() {
        // одиничне коло в (0, 0); прямі y = 0, y = 1, y = 2 у вигляді a·x + b·y + c = 0
        assertEquals("Result: Two points of intersection",
                capture(() -> Intersections.calculateCircleLine(0, 0, 1, 0, 1, 0)));
        assertEquals("Result: One point of tangency",
                capture(() -> Intersections.calculateCircleLine(0, 0, 1, 0, 1, -1)));
        assertEquals("Result: No common points",
                capture(() -> Intersections.calculateCircleLine(0, 0, 1, 0, 1, -2)));
    }

    @Test
    @DisplayName("circle-line: зміщений центр і похила пряма x + y = 0")
    void circleLineShiftedCenter() {
        // центр (1, 1), відстань до x + y = 0 дорівнює √2
        assertEquals("Result: No common points",
                capture(() -> Intersections.calculateCircleLine(1, 1, 1, 1, 1, 0)));
        assertEquals("Result: Two points of intersection",
                capture(() -> Intersections.calculateCircleLine(1, 1, 2, 1, 1, 0)));
    }

    @Test
    @DisplayName("circle-line: a = b = 0 або r < 0 → помилка")
    void circleLineInvalid() {
        assertEquals(INVALID, capture(() -> Intersections.calculateCircleLine(0, 0, 1, 0, 0, 5)));
        assertEquals(INVALID, capture(() -> Intersections.calculateCircleLine(0, 0, -1, 1, 1, 0)));
    }

    // ===================================================================
    // circles-intersect (task 16)
    // ===================================================================

    @Test
    @DisplayName("circles-intersect: перетин, дотик ззовні, далеко, одне всередині іншого")
    void circlesIntersect() {
        assertEquals("Result: true", capture(() -> Intersections.calculateCirclesIntersect(0, 0, 1, 1, 0, 1)));
        assertEquals("Result: true", capture(() -> Intersections.calculateCirclesIntersect(0, 0, 1, 2, 0, 1)));
        assertEquals("Result: false", capture(() -> Intersections.calculateCirclesIntersect(0, 0, 1, 3, 0, 1)));
        assertEquals("Result: false", capture(() -> Intersections.calculateCirclesIntersect(0, 0, 5, 0, 0, 1)));
    }

    @Test
    @DisplayName("circles-intersect: внутрішній дотик → true, від'ємний радіус → помилка")
    void circlesIntersectEdgeCases() {
        assertEquals("Result: true", capture(() -> Intersections.calculateCirclesIntersect(0, 0, 2, 1, 0, 1)));
        assertEquals(INVALID, capture(() -> Intersections.calculateCirclesIntersect(0, 0, -1, 1, 0, 1)));
    }

    // ===================================================================
    // squares-intersect (task 17): лівий нижній кут і сторона
    // ===================================================================

    @Test
    @DisplayName("squares-intersect: прямокутник перетину")
    void squaresIntersect() {
        assertEquals("Result: Intersect rect: Bottom-Left (1.000000, 1.000000), Top-Right (2.000000, 2.000000)",
                capture(() -> Intersections.calculateSquaresIntersect(0, 0, 2, 1, 1, 2)));
    }

    @Test
    @DisplayName("squares-intersect: дотик по стороні дає вироджений прямокутник")
    void squaresTouch() {
        assertEquals("Result: Intersect rect: Bottom-Left (1.000000, 0.000000), Top-Right (1.000000, 1.000000)",
                capture(() -> Intersections.calculateSquaresIntersect(0, 0, 1, 1, 0, 1)));
    }

    @Test
    @DisplayName("squares-intersect: немає перетину, від'ємна сторона → помилка")
    void squaresNoIntersection() {
        assertEquals("Result: Squares do not intersect",
                capture(() -> Intersections.calculateSquaresIntersect(0, 0, 1, 5, 5, 1)));
        assertEquals(INVALID, capture(() -> Intersections.calculateSquaresIntersect(0, 0, -1, 5, 5, 1)));
    }

    // ===================================================================
    // rect-bounding-box (task 18): (x1,y1)-(x2,y2) і (x3,y3)-(x4,y4)
    // ===================================================================

    @Test
    @DisplayName("rect-bounding-box: мінімальний прямокутник, що містить обидва")
    void rectBoundingBox() {
        assertEquals("Result: Bounding box: Bottom-Left (0.000000, 0.000000), Top-Right (3.000000, 3.000000)",
                capture(() -> Intersections.calculateRectBoundingBox(0, 0, 1, 1, 2, 2, 3, 3)));
        // вкладений прямокутник не змінює рамку
        assertEquals("Result: Bounding box: Bottom-Left (-5.000000, -5.000000), Top-Right (5.000000, 5.000000)",
                capture(() -> Intersections.calculateRectBoundingBox(-5, -5, 5, 5, 0, 0, 1, 1)));
    }

    @Test
    @DisplayName("rect-bounding-box: кути в неправильному порядку → помилка")
    void rectBoundingBoxInvalid() {
        assertEquals(INVALID, capture(() -> Intersections.calculateRectBoundingBox(1, 0, 0, 1, 2, 2, 3, 3)));
        assertEquals(INVALID, capture(() -> Intersections.calculateRectBoundingBox(0, 0, 1, 1, 2, 3, 3, 2)));
    }
}
