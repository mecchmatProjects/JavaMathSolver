package solver.app;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static solver.testutil.StdOut.capture;

/**
 * CORE-11: кожна команда каталогу доходить до свого методу через dispatch.
 * Перевіряє маршрутизацію наскрізно (dispatch → метод → Output), формули —
 * у тестах пакетів algebra / geometry.
 */
class CommandRoutingTest {

    private static String dispatch(String command, Number... args) {
        return capture(() -> CommandDispatcher.dispatch(command, args));
    }

    // ===================================================================
    // Ряди Тейлора: аліас == канонічна назва (усі 14 пар)
    // ===================================================================

    static Stream<Arguments> taylorAliases() {
        return Stream.of(
                Arguments.of("taylor-sin", "sin-taylor", 1.0),
                Arguments.of("taylor-cos", "series-b", 1.0),
                Arguments.of("taylor-sinh", "series-c", 1.0),
                Arguments.of("taylor-cosh", "series-h", 1.0),
                Arguments.of("taylor-exp", "series-e", 1.0),
                Arguments.of("taylor-ln-one-plus-x", "series-ln", 0.5),
                Arguments.of("taylor-geom-series", "series-ye", 0.5),
                Arguments.of("taylor-artanh", "series-j", 0.5),
                Arguments.of("taylor-sqrt-one-plus-x", "series-k", 0.5),
                Arguments.of("taylor-inv-sqrt-one-plus-x", "series-l", 0.5),
                Arguments.of("taylor-asin", "series-m", 0.5),
                Arguments.of("taylor-inv-sq", "series-z", 0.5),
                Arguments.of("taylor-inv-cube", "series-i", 0.5),
                Arguments.of("taylor-inv-one-plus-x2", "series-y", 0.5));
    }

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("taylorAliases")
    @DisplayName("Аліас ряду Тейлора дає той самий результат, що й канонічна назва")
    void aliasMatchesCanonical(String canonical, String alias, double x) {
        String expected = dispatch(canonical, x, 1e-10);
        assertTrue(expected.startsWith("Result: "), canonical + " → " + expected);
        assertEquals(expected, dispatch(alias, x, 1e-10));
    }

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("taylorAliases")
    @DisplayName("Аліас ряду Тейлора відхиляє ті самі некоректні дані")
    void aliasRejectsSameInput(String canonical, String alias, double x) {
        assertEquals(dispatch(canonical, x, 0.0), dispatch(alias, x, 0.0));
        assertEquals("Error: Invalid mathematical input", dispatch(alias, x, 0.0));
    }

    // ===================================================================
    // Геометрія Lab 1-2: dispatch доходить до формули
    // ===================================================================

    static Stream<Arguments> geometryCommands() {
        return Stream.of(
                Arguments.of("distance", new Number[]{1L, 2L, 4L, 6L}, "Result: 5.000000"),
                Arguments.of("origin-distance", new Number[]{3L, 4L}, "Result: 5.000000"),
                Arguments.of("manhattan-distance", new Number[]{1L, 2L, 4L, 6L}, "Result: 7.000000"),
                Arguments.of("midpoint", new Number[]{0L, 0L, 2L, 4L}, "Result: (1.000000, 2.000000)"),
                Arguments.of("quadrant", new Number[]{-1L, 1L}, "Result: II"),
                Arguments.of("collinear", new Number[]{0L, 0L, 1L, 1L, 2L, 2L}, "Result: true"),
                Arguments.of("circle-area", new Number[]{0L}, "Result: 0.000000"),
                Arguments.of("circle-circumference", new Number[]{0L}, "Result: 0.000000"),
                Arguments.of("rectangle-area", new Number[]{3L, 4L}, "Result: 12.000000"),
                Arguments.of("rectangle-perimeter", new Number[]{3L, 4L}, "Result: 14.000000"),
                Arguments.of("ellipse-area", new Number[]{0L, 5L}, "Result: 0.000000"),
                Arguments.of("triangle-valid", new Number[]{3L, 4L, 5L}, "Result: true"),
                Arguments.of("triangle-area", new Number[]{3L, 4L, 5L}, "Result: 6.000000"),
                Arguments.of("triangle-medians", new Number[]{3L, 4L, 5L},
                        "Result: m_a=4.272002, m_b=3.605551, m_c=2.500000"),
                Arguments.of("triangle-bisectors", new Number[]{1L, 2L, 3L}, "Error: Invalid mathematical input"),
                Arguments.of("triangle-heights", new Number[]{3L, 4L, 5L},
                        "Result: h_a=4.000000, h_b=3.000000, h_c=2.400000"),
                Arguments.of("triangle-angles", new Number[]{1L, 2L, 3L}, "Error: Invalid mathematical input"),
                Arguments.of("triangle-area-inradius", new Number[]{1L, 1L, 1L, 1L}, "Error: Invalid mathematical input"),
                Arguments.of("cylinder-volume", new Number[]{2L, 0L}, "Result: 0.000000"),
                Arguments.of("cone-volume", new Number[]{0L, 2L}, "Result: 0.000000"),
                Arguments.of("torus-volume", new Number[]{2L, 2L}, "Result: 0.000000"),
                Arguments.of("circle-segment", new Number[]{5L, 6L, 0L, 1L}, "Result: 0"),
                Arguments.of("circle-line", new Number[]{0L, 0L, 1L, 0L, 1L, -2L}, "Result: No common points"),
                Arguments.of("circles-intersect", new Number[]{0L, 0L, 1L, 3L, 0L, 1L}, "Result: false"),
                Arguments.of("squares-intersect", new Number[]{0L, 0L, 1L, 5L, 5L, 1L}, "Result: Squares do not intersect"),
                Arguments.of("rect-bounding-box", new Number[]{0L, 0L, 1L, 1L, 2L, 2L, 3L, 3L},
                        "Result: Bounding box: Bottom-Left (0.000000, 0.000000), Top-Right (3.000000, 3.000000)"),
                Arguments.of("polygon", new Number[]{0L, 0L, 1L, 0L, 1L, 1L, 0L, 1L},
                        "Result: perimeter=4.000000, convex=true"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("geometryCommands")
    @DisplayName("Геометрична команда маршрутизується до свого методу")
    void geometryCommandIsRouted(String command, Number[] args, String expected) {
        assertEquals(expected, dispatch(command, args));
    }

    // ===================================================================
    // Алгебра Lab 1-2: dispatch доходить до методу
    // ===================================================================

    static Stream<Arguments> algebraCommands() {
        return Stream.of(
                Arguments.of("add", new Number[]{2L, 3L}, "Result: 5.000000"),
                Arguments.of("sub", new Number[]{2L, 3L}, "Result: -1.000000"),
                Arguments.of("mul", new Number[]{4L, 5L}, "Result: 20.000000"),
                Arguments.of("pow", new Number[]{2L, 10L}, "Result: 1024.000000"),
                Arguments.of("abs", new Number[]{-7.25}, "Result: 7.250000"),
                Arguments.of("solve-linear", new Number[]{2L, -6L}, "Result: 3.000000"),
                Arguments.of("solve-quadratic", new Number[]{1L, -5L, 6L}, "Result: x1=2.000000, x2=3.000000"),
                Arguments.of("max3", new Number[]{1L, 7L, 3L}, "Result: 7.000000"),
                Arguments.of("gcd", new Number[]{48L, 18L}, "Result: 6"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algebraCommands")
    @DisplayName("Алгебраїчна команда маршрутизується до свого методу")
    void algebraCommandIsRouted(String command, Number[] args, String expected) {
        assertEquals(expected, dispatch(command, args));
    }
}
