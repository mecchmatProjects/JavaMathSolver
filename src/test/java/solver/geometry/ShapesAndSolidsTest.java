package solver.geometry;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * GEO-02 ... GEO-05, Lab 2 tasks 7, 11-13: площі, периметри, об'єми тіл.
 */
class ShapesAndSolidsTest {

    private static final String INVALID = "Error: Invalid mathematical input";

    private static String result(double expected) {
        return String.format(Locale.ROOT, "Result: %f", expected);
    }

    // ===================================================================
    // Коло (GEO-02, GEO-03)
    // ===================================================================

    @Test
    @DisplayName("circle-area: π r²")
    void circleArea() {
        assertEquals(result(Math.PI), capture(() -> Shapes.calculateCircleArea(1)));
        assertEquals(result(25 * Math.PI), capture(() -> Shapes.calculateCircleArea(5)));
        assertEquals("Result: 0.000000", capture(() -> Shapes.calculateCircleArea(0)));
    }

    @Test
    @DisplayName("circle-circumference: 2π r")
    void circleCircumference() {
        assertEquals(result(2 * Math.PI), capture(() -> Shapes.calculateCircleCircumference(1)));
        assertEquals(result(10 * Math.PI), capture(() -> Shapes.calculateCircleCircumference(5)));
        assertEquals("Result: 0.000000", capture(() -> Shapes.calculateCircleCircumference(0)));
    }

    @Test
    @DisplayName("коло: від'ємний радіус → помилка")
    void circleNegativeRadius() {
        assertEquals(INVALID, capture(() -> Shapes.calculateCircleArea(-1)));
        assertEquals(INVALID, capture(() -> Shapes.calculateCircleCircumference(-0.001)));
    }

    @Test
    @DisplayName("circle-area: переповнення → помилка, а не Infinity")
    void circleAreaOverflow() {
        assertEquals(INVALID, capture(() -> Shapes.calculateCircleArea(1e200)));
    }

    // ===================================================================
    // Прямокутник (GEO-04, GEO-05)
    // ===================================================================

    @Test
    @DisplayName("rectangle-area / rectangle-perimeter")
    void rectangle() {
        assertEquals("Result: 12.000000", capture(() -> Shapes.calculateRectangleArea(3, 4)));
        assertEquals("Result: 14.000000", capture(() -> Shapes.calculateRectanglePerimeter(3, 4)));
        assertEquals("Result: 0.000000", capture(() -> Shapes.calculateRectangleArea(0, 4)));
        assertEquals("Result: 8.000000", capture(() -> Shapes.calculateRectanglePerimeter(0, 4))); // вироджений
    }

    @Test
    @DisplayName("прямокутник: від'ємна сторона → помилка")
    void rectangleNegativeSide() {
        assertEquals(INVALID, capture(() -> Shapes.calculateRectangleArea(-3, 4)));
        assertEquals(INVALID, capture(() -> Shapes.calculateRectangleArea(3, -4)));
        assertEquals(INVALID, capture(() -> Shapes.calculateRectanglePerimeter(-3, 4)));
        assertEquals(INVALID, capture(() -> Shapes.calculateRectanglePerimeter(3, -4)));
    }

    // ===================================================================
    // Еліпс (task 7)
    // ===================================================================

    @Test
    @DisplayName("ellipse-area: π a b, при a = b збігається з площею круга")
    void ellipseArea() {
        assertEquals(result(6 * Math.PI), capture(() -> Shapes.calculateEllipseArea(2, 3)));
        assertEquals(capture(() -> Shapes.calculateCircleArea(4)), capture(() -> Shapes.calculateEllipseArea(4, 4)));
        assertEquals(INVALID, capture(() -> Shapes.calculateEllipseArea(-2, 3)));
    }

    // ===================================================================
    // Тіла (tasks 11-13)
    // ===================================================================

    @Test
    @DisplayName("cylinder-volume: π r² h")
    void cylinderVolume() {
        assertEquals(result(Math.PI), capture(() -> Solids.calculateCylinderVolume(1, 1)));
        assertEquals(result(Math.PI * 4 * 5), capture(() -> Solids.calculateCylinderVolume(2, 5)));
        assertEquals("Result: 0.000000", capture(() -> Solids.calculateCylinderVolume(2, 0)));
    }

    @Test
    @DisplayName("cone-volume: третина об'єму циліндра")
    void coneVolume() {
        assertEquals(result(3 * Math.PI), capture(() -> Solids.calculateConeVolume(3, 1)));
        assertEquals(result(Math.PI * 4 * 6 / 3), capture(() -> Solids.calculateConeVolume(2, 6)));
    }

    @Test
    @DisplayName("torus-volume: 2π² R r², R і r з внутрішнього та зовнішнього радіусів")
    void torusVolume() {
        // r_in = 1, r_out = 3 → трубка r = 1, центр R = 2 → 4π²
        assertEquals(result(4 * Math.PI * Math.PI), capture(() -> Solids.calculateTorusVolume(1, 3)));
        // r_in = r_out → трубка нульова
        assertEquals("Result: 0.000000", capture(() -> Solids.calculateTorusVolume(2, 2)));
    }

    @Test
    @DisplayName("тіла: від'ємні розміри і r_in > r_out → помилка")
    void solidsInvalidInput() {
        assertEquals(INVALID, capture(() -> Solids.calculateCylinderVolume(-1, 1)));
        assertEquals(INVALID, capture(() -> Solids.calculateCylinderVolume(1, -1)));
        assertEquals(INVALID, capture(() -> Solids.calculateConeVolume(-1, 1)));
        assertEquals(INVALID, capture(() -> Solids.calculateConeVolume(1, -1)));
        assertEquals(INVALID, capture(() -> Solids.calculateTorusVolume(-1, 3)));
        assertEquals(INVALID, capture(() -> Solids.calculateTorusVolume(3, 1)));
    }

    @Test
    @DisplayName("тіла: переповнення → помилка")
    void solidsOverflow() {
        assertEquals(INVALID, capture(() -> Solids.calculateCylinderVolume(1e200, 1e200)));
        assertEquals(INVALID, capture(() -> Solids.calculateConeVolume(1e200, 1e200)));
        assertEquals(INVALID, capture(() -> Solids.calculateTorusVolume(0, 1e200)));
    }
}
