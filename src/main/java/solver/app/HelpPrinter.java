package solver.app;

import java.util.Locale;
import static solver.app.MathSolver.VERSION;

/**
 * CORE-16: довідка.
 */
public final class HelpPrinter {

    private HelpPrinter() {
    }

    // CORE-04 / CORE-16: довідка
    public static void printHelp() {
        System.out.println("JavaMathSolver v" + VERSION);
        System.out.println("Usage: java -cp target/classes solver.app.MathSolver <command> <arguments>");

        System.out.println();
        System.out.println("Algebra:");
        printHelpLine("add a b", "a + b");
        printHelpLine("sub a b", "a - b");
        printHelpLine("mul a b", "a * b");
        printHelpLine("div a b", "a / b");
        printHelpLine("pow a b", "a ^ b");
        printHelpLine("sqrt x", "square root of x");
        printHelpLine("abs x", "|x|");
        printHelpLine("solve-linear a b", "solve a*x + b = 0");
        printHelpLine("solve-quadratic a b c", "solve a*x^2 + b*x + c = 0");
        printHelpLine("max3 a b c", "maximum of three numbers");
        printHelpLine("gcd a b", "greatest common divisor (integers)");
        printHelpLine("factorial n", "n!, 0 <= n <= 20");
        printHelpLine("fibonacci n", "F(n), 0 <= n <= 92");

        System.out.println();
        System.out.println("Taylor series (x eps; alias in brackets):");
        printHelpLine("taylor-sin x eps", "sin(x) [sin-taylor]");
        printHelpLine("taylor-cos x eps", "cos(x) [series-b]");
        printHelpLine("taylor-sinh x eps", "sinh(x) [series-c]");
        printHelpLine("taylor-cosh x eps", "cosh(x) [series-h]");
        printHelpLine("taylor-exp x eps", "e^x [series-e]");
        printHelpLine("taylor-ln-one-plus-x x eps", "ln(1 + x), |x| < 1 [series-ln]");
        printHelpLine("taylor-geom-series x eps", "1 / (1 + x), |x| < 1 [series-ye]");
        printHelpLine("taylor-artanh x eps", "artanh(x), |x| < 1 [series-j]");
        printHelpLine("taylor-sqrt-one-plus-x x eps", "sqrt(1 + x), |x| < 1 [series-k]");
        printHelpLine("taylor-inv-sqrt-one-plus-x x eps", "1 / sqrt(1 + x), |x| < 1 [series-l]");
        printHelpLine("taylor-asin x eps", "arcsin(x), |x| < 1 [series-m]");
        printHelpLine("taylor-inv-sq x eps", "1 / (1 + x)^2, |x| < 1 [series-z]");
        printHelpLine("taylor-inv-cube x eps", "1 / (1 + x)^3, |x| < 1 [series-i]");
        printHelpLine("taylor-inv-one-plus-x2 x eps", "1 / (1 + x^2), |x| < 1 [series-y]");

        System.out.println();
        System.out.println("Geometry:");
        printHelpLine("distance x1 y1 x2 y2", "distance between two points");
        printHelpLine("origin-distance x y", "distance from (0, 0)");
        printHelpLine("manhattan-distance x1 y1 x2 y2", "|x2 - x1| + |y2 - y1|");
        printHelpLine("midpoint x1 y1 x2 y2", "midpoint of a segment");
        printHelpLine("quadrant x y", "I, II, III, IV, AXIS or ORIGIN");
        printHelpLine("collinear x1 y1 x2 y2 x3 y3", "are three points on one line");
        printHelpLine("circle-area r", "pi * r^2");
        printHelpLine("circle-circumference r", "2 * pi * r");
        printHelpLine("rectangle-area a b", "a * b");
        printHelpLine("rectangle-perimeter a b", "2 * (a + b)");
        printHelpLine("ellipse-area a b", "pi * a * b");
        printHelpLine("triangle-valid a b c", "can sides a, b, c form a triangle");
        printHelpLine("triangle-area a b c", "area by Heron's formula");
        printHelpLine("triangle-medians a b c", "medians m_a, m_b, m_c");
        printHelpLine("triangle-bisectors a b c", "bisectors l_a, l_b, l_c");
        printHelpLine("triangle-heights a b c", "heights h_a, h_b, h_c");
        printHelpLine("triangle-angles a b c", "angles in radians and degrees");
        printHelpLine("triangle-area-inradius A B C r", "area by angles (rad) and inradius");
        printHelpLine("cylinder-volume r h", "pi * r^2 * h");
        printHelpLine("cone-volume r h", "pi * r^2 * h / 3");
        printHelpLine("torus-volume r_in r_out", "torus volume by inner and outer radius");
        printHelpLine("circle-segment r x y_min len", "circle x^2 + y^2 = r^2 and vertical segment");
        printHelpLine("circle-line cx cy r a b c", "circle and line a*x + b*y + c = 0");
        printHelpLine("circles-intersect x1 y1 r1 x2 y2 r2", "do two circles intersect");
        printHelpLine("squares-intersect x1 y1 s1 x2 y2 s2", "intersection of two squares");
        printHelpLine("rect-bounding-box x1 y1 x2 y2 x3 y3 x4 y4", "bounding box of two rectangles");
        printHelpLine("polygon x1 y1 x2 y2 x3 y3 ...", "perimeter and convexity (>= 3 vertices)");
        printHelpLine("monte-carlo-triangle n", "probability that 3 random sides form a triangle");

        System.out.println();
        System.out.println("Expressions (quote the expression or separate tokens with spaces):");
        printHelpLine("tokenize \"expr\"", "list tokens: index, type, lexeme");
        printHelpLine("token-stats \"expr\"", "count numbers, identifiers, operators, parentheses");
        printHelpLine("validate-expr \"expr\"", "check that every token is recognised");

        System.out.println();
        printHelpLine("help", "show this help");
    }

    private static void printHelpLine(String usage, String description) {
        System.out.printf(Locale.ROOT, "  %-42s %s%n", usage, description);
    }
}
