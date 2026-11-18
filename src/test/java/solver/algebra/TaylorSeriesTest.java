package solver.algebra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static solver.core.Messages.ERR_INVALID_INPUT;

class TaylorSeriesTest {

    private static final double EPS = 1e-9;
    private static final double ABS_TOL = 1e-6;
    private static final double REL_TOL = 1e-9;
    private static final Pattern NUMBER =
            Pattern.compile("[-+]?\\d+(?:[.,]\\d+)?(?:[eE][-+]?\\d+)?");

    @FunctionalInterface
    interface Series {
        void calc(double x, double eps);
    }

    @FunctionalInterface
    interface Reference {
        double at(double x);
    }


    private static String capture(Runnable r) {
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf, true, StandardCharsets.UTF_8);
        System.setOut(ps);
        System.setErr(ps);
        try {
            r.run();
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
        return buf.toString(StandardCharsets.UTF_8);
    }

    private static double result(Series s, double x, double eps) {
        String out = capture(() -> s.calc(x, eps));
        Matcher m = NUMBER.matcher(out);
        assertTrue(m.find(), "У виводі немає числа: \"" + out + "\"");
        return Double.parseDouble(m.group().replace(',', '.'));
    }

    private static void assertError(Series s, double x, double eps) {
        String out = capture(() -> s.calc(x, eps));
        assertTrue(out.contains(String.valueOf(ERR_INVALID_INPUT)),
                "Очікувалась помилка для x=" + x + ", eps=" + eps + ", а вивід: \"" + out + "\"");
    }


    private static Stream<Arguments> cross(String name, Series s, Reference ref, double... xs) {
        return DoubleStream.of(xs).mapToObj(x -> Arguments.of(name, s, ref, x));
    }

    static Stream<Arguments> infiniteSeriesCases() {
        double[] angles = {-1000, -10, -Math.PI, -1, 0, 0.5, Math.PI, 4, 100, 1000};
        double[] xs = {-10, -3.5, -1, -0.1, 0, 0.1, 1, 2.5, 7, 10};
        return Stream.of(
                cross("sin", TaylorSeries::calculateTaylorSin, Math::sin, angles),
                cross("cos", TaylorSeries::calculateTaylorCos, Math::cos, angles),
                cross("sinh", TaylorSeries::calculateTaylorSinh, Math::sinh, xs),
                cross("cosh", TaylorSeries::calculateTaylorCosh, Math::cosh, xs),
                cross("exp", TaylorSeries::calculateTaylorExp, Math::exp, xs)
        ).flatMap(Function.identity());
    }

    static Stream<Arguments> restrictedSeriesCases() {
        double[] xs = {-0.7, -0.3, -0.05, 0, 0.2, 0.5, 0.7};
        return Stream.of(
                cross("ln(1+x)", TaylorSeries::calculateTaylorLnOnePlusX, Math::log1p, xs),
                cross("1/(1+x)", TaylorSeries::calculateTaylorGeomSeries, x -> 1.0 / (1.0 + x), xs),
                cross("artanh", TaylorSeries::calculateTaylorArtanh,
                        x -> 0.5 * Math.log((1.0 + x) / (1.0 - x)), xs),
                cross("sqrt(1+x)", TaylorSeries::calculateTaylorSqrtOnePlusX,
                        x -> Math.sqrt(1.0 + x), xs),
                cross("1/sqrt(1+x)", TaylorSeries::calculateTaylorInvSqrtOnePlusX,
                        x -> 1.0 / Math.sqrt(1.0 + x), xs),
                cross("asin", TaylorSeries::calculateTaylorAsin, Math::asin, xs),
                cross("1/(1+x)^2", TaylorSeries::calculateTaylorInvSq,
                        x -> 1.0 / ((1.0 + x) * (1.0 + x)), xs),
                cross("1/(1+x)^3", TaylorSeries::calculateTaylorInvCube,
                        x -> 1.0 / Math.pow(1.0 + x, 3), xs),
                cross("1/(1+x^2)", TaylorSeries::calculateTaylorInvOnePlusX2,
                        x -> 1.0 / (1.0 + x * x), xs)
        ).flatMap(Function.identity());
    }

    static Stream<Arguments> restrictedSeries() {
        return restrictedSeriesCases()
                .map(a -> Arguments.of(a.get()[0], a.get()[1]))
                .distinct();
    }

    static Stream<Arguments> allSeries() {
        return Stream.concat(
                infiniteSeriesCases().map(a -> Arguments.of(a.get()[0], a.get()[1])).distinct(),
                restrictedSeries());
    }


    @ParameterizedTest(name = "{0}, x = {3}")
    @MethodSource({"infiniteSeriesCases", "restrictedSeriesCases"})
    void matchesReferenceValue(String name, Series s, Reference ref, double x) {
        double expected = ref.at(x);
        double actual = result(s, x, EPS);
        double tol = ABS_TOL + REL_TOL * Math.abs(expected);
        assertEquals(expected, actual, tol, name + " при x=" + x);
    }

    @ParameterizedTest(name = "{0}: нескінченний або NaN x")
    @MethodSource("allSeries")
    void rejectsNonFiniteX(String name, Series s) {
        assertError(s, Double.NaN, EPS);
        assertError(s, Double.POSITIVE_INFINITY, EPS);
        assertError(s, Double.NEGATIVE_INFINITY, EPS);
    }

    @ParameterizedTest(name = "{0}: некоректний eps")
    @MethodSource("allSeries")
    void rejectsBadEps(String name, Series s) {
        assertError(s, 0.3, 0.0);
        assertError(s, 0.3, -1e-9);
        assertError(s, 0.3, Double.NaN);
        assertError(s, 0.3, Double.POSITIVE_INFINITY);
    }

    @ParameterizedTest(name = "{0}: |x| >= 1")
    @MethodSource("restrictedSeries")
    void restrictedSeriesRejectXOutsideRadius(String name, Series s) {
        for (double x : new double[]{1.0, -1.0, 1.5, -2.0, 100.0}) {
            assertError(s, x, EPS);
        }
    }

    @ParameterizedTest(name = "{0}: дуже малий eps не зависає")
    @MethodSource("allSeries")
    void tinyEpsTerminates(String name, Series s) {
        assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> capture(() -> s.calc(0.5, 1e-300)));
    }

    @Test
    void knownValuesAtZero() {
        assertEquals(0.0, result(TaylorSeries::calculateTaylorSin, 0.0, EPS), ABS_TOL);
        assertEquals(1.0, result(TaylorSeries::calculateTaylorCos, 0.0, EPS), ABS_TOL);
        assertEquals(1.0, result(TaylorSeries::calculateTaylorExp, 0.0, EPS), ABS_TOL);
    }
}