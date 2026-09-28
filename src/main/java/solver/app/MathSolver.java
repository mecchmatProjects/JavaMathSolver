package solver.app;

import java.math.BigInteger;
import java.util.Locale;
import java.util.regex.Pattern;

public class MathSolver {

    private static final String VERSION = "0.2";
    private static final int MAX_SERIES_ITERATIONS = 10_000_000;

    // CORE-12: службові значення арності
    private static final int UNKNOWN_COMMAND = -1;
    private static final int VARIADIC = -2;
    private static final int VARIADIC_MIN_ARGS = 6;

    // CORE-15: стандартизовані повідомлення
    private static final String ERR_UNKNOWN_COMMAND = "Unknown command: ";
    private static final String ERR_INVALID_NUMBER = "Invalid number: ";
    private static final String ERR_NOT_ENOUGH_ARGS = "Error: Not enough arguments";
    private static final String ERR_TOO_MANY_ARGS = "Error: Too many arguments";
    private static final String ERR_DIVISION_BY_ZERO = "Error: Division by zero";
    private static final String ERR_INVALID_INPUT = "Error: Invalid mathematical input";
    private static final String HINT_HELP = "Use 'help' to see available commands.";

    // CORE-13: допустимі формати чисел
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[+-]?\\d+");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("JavaMathSolver v" + VERSION);
            System.out.println(HINT_HELP);
            return;
        }

        String command = args[0].trim().toLowerCase(Locale.ROOT);

        int expected = getExpectedArgsCount(command);
        if (expected == UNKNOWN_COMMAND) {
            printUnknownCommand(args[0]);
            return;
        }

        if (!validateArgs(args.length - 1, expected)) {
            return;
        }

        Number[] arguments = parseArguments(args);
        if (arguments == null) {
            return;
        }

        dispatch(command, arguments);
    }

    // CORE-11: диспетчеризація команд
    public static void dispatch(String command, Number[] arguments) {
        double[] a = toDoubleArray(arguments);

        switch (command) {
            case "help":
                printHelp();
                break;

            // --- ALGEBRA: Lab 1 ---
            case "add":
                printResult(add(a[0], a[1]));
                break;
            case "sub":
                printResult(sub(a[0], a[1]));
                break;
            case "mul":
                printResult(mul(a[0], a[1]));
                break;
            case "div":
                if (a[1] == 0.0) {
                    printError(ERR_DIVISION_BY_ZERO);
                } else {
                    printResult(div(a[0], a[1]));
                }
                break;
            case "pow":
                printResult(pow(a[0], a[1]));
                break;
            case "abs":
                printResult(abs(a[0]));
                break;
            case "sqrt":
                if (a[0] < 0.0) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    printResult(sqrt(a[0]));
                }
                break;

            // --- ALGEBRA: Lab 2 ---
            case "solve-linear":
                solveLinear(a[0], a[1]);
                break;
            case "solve-quadratic":
                solveQuadratic(a[0], a[1], a[2]);
                break;
            case "max3":
                printResult(max3(a[0], a[1], a[2]));
                break;
            case "gcd": {
                Long x = toLongExact(arguments[0]);
                Long y = toLongExact(arguments[1]);
                if (x == null || y == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    solveGcd(x, y);
                }
                break;
            }
            case "factorial": {
                Long n = toLongExact(arguments[0]);
                if (n == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    factorial(n);
                }
                break;
            }
            case "fibonacci": {
                Long n = toLongExact(arguments[0]);
                if (n == null) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    fibonacci(n);
                }
                break;
            }

            // --- ALGEBRA: Taylor series (канонічна назва + аліас) ---
            case "taylor-sin":
            case "sin-taylor":
                calculateTaylorSin(a[0], a[1]);
                break;
            case "taylor-cos":
            case "series-b":
                calculateTaylorCos(a[0], a[1]);
                break;
            case "taylor-sinh":
            case "series-c":
                calculateTaylorSinh(a[0], a[1]);
                break;
            case "taylor-cosh":
            case "series-h":
                calculateTaylorCosh(a[0], a[1]);
                break;
            case "taylor-exp":
            case "series-e":
                calculateTaylorExp(a[0], a[1]);
                break;
            case "taylor-ln-one-plus-x":
            case "series-ln":
                calculateTaylorLnOnePlusX(a[0], a[1]);
                break;
            case "taylor-geom-series":
            case "series-ye":
                calculateTaylorGeomSeries(a[0], a[1]);
                break;
            case "taylor-artanh":
            case "series-j":
                calculateTaylorArtanh(a[0], a[1]);
                break;
            case "taylor-sqrt-one-plus-x":
            case "series-k":
                calculateTaylorSqrtOnePlusX(a[0], a[1]);
                break;
            case "taylor-inv-sqrt-one-plus-x":
            case "series-l":
                calculateTaylorInvSqrtOnePlusX(a[0], a[1]);
                break;
            case "taylor-asin":
            case "series-m":
                calculateTaylorAsin(a[0], a[1]);
                break;
            case "taylor-inv-sq":
            case "series-z":
                calculateTaylorInvSq(a[0], a[1]);
                break;
            case "taylor-inv-cube":
            case "series-i":
                calculateTaylorInvCube(a[0], a[1]);
                break;
            case "taylor-inv-one-plus-x2":
            case "series-y":
                calculateTaylorInvOnePlusX2(a[0], a[1]);
                break;

            // --- GEOMETRY: Lab 1 ---
            case "distance":
                calculateDistance(a[0], a[1], a[2], a[3]);
                break;
            case "origin-distance":
                calculateOriginDistance(a[0], a[1]);
                break;
            case "circle-area":
                calculateCircleArea(a[0]);
                break;
            case "circle-circumference":
                calculateCircleCircumference(a[0]);
                break;
            case "rectangle-area":
                calculateRectangleArea(a[0], a[1]);
                break;
            case "rectangle-perimeter":
                calculateRectanglePerimeter(a[0], a[1]);
                break;

            // --- GEOMETRY: Lab 2 ---
            case "triangle-area":
                calculateTriangleArea(a[0], a[1], a[2]);
                break;
            case "triangle-valid":
                calculateTriangleValid(a[0], a[1], a[2]);
                break;
            case "quadrant":
                calculateQuadrant(a[0], a[1]);
                break;
            case "manhattan-distance":
                calculateManhattanDistance(a[0], a[1], a[2], a[3]);
                break;
            case "midpoint":
                calculateMidpoint(a[0], a[1], a[2], a[3]);
                break;
            case "collinear":
                calculateCollinear(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "ellipse-area":
                calculateEllipseArea(a[0], a[1]);
                break;
            case "triangle-medians":
                calculateMedians(a[0], a[1], a[2]);
                break;
            case "triangle-bisectors":
                calculateBisectors(a[0], a[1], a[2]);
                break;
            case "triangle-heights":
                calculateHeights(a[0], a[1], a[2]);
                break;
            case "triangle-area-inradius":
                calculateAreaByAnglesAndInradius(a[0], a[1], a[2], a[3]);
                break;
            case "triangle-angles":
                calculateTriangleAngles(a[0], a[1], a[2]);
                break;
            case "cylinder-volume":
                calculateCylinderVolume(a[0], a[1]);
                break;
            case "cone-volume":
                calculateConeVolume(a[0], a[1]);
                break;
            case "torus-volume":
                calculateTorusVolume(a[0], a[1]);
                break;
            case "circle-segment":
                calculateCircleSegmentIntersections(a[0], a[1], a[2], a[3]);
                break;
            case "circle-line":
                calculateCircleLine(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "circles-intersect":
                calculateCirclesIntersect(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "squares-intersect":
                calculateSquaresIntersect(a[0], a[1], a[2], a[3], a[4], a[5]);
                break;
            case "rect-bounding-box":
                calculateRectBoundingBox(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]);
                break;
            case "polygon":
                processPolygon(a);
                break;
            case "monte-carlo-triangle": {
                Long n = toLongExact(arguments[0]);
                if (n == null || n <= 0 || n > Integer.MAX_VALUE) {
                    printError(ERR_INVALID_INPUT);
                } else {
                    calculateMonteCarloTriangle(n.intValue());
                }
                break;
            }

            default:
                printUnknownCommand(command);
                break;
        }
    }

    // CORE-12: очікувана кількість аргументів
    public static int getExpectedArgsCount(String command) {
        switch (command) {
            case "help":
                return 0;

            case "abs":
            case "sqrt":
            case "factorial":
            case "fibonacci":
            case "circle-area":
            case "circle-circumference":
            case "monte-carlo-triangle":
                return 1;

            case "add":
            case "sub":
            case "mul":
            case "div":
            case "pow":
            case "solve-linear":
            case "gcd":
            case "taylor-sin":
            case "sin-taylor":
            case "taylor-cos":
            case "series-b":
            case "taylor-sinh":
            case "series-c":
            case "taylor-cosh":
            case "series-h":
            case "taylor-exp":
            case "series-e":
            case "taylor-ln-one-plus-x":
            case "series-ln":
            case "taylor-geom-series":
            case "series-ye":
            case "taylor-artanh":
            case "series-j":
            case "taylor-sqrt-one-plus-x":
            case "series-k":
            case "taylor-inv-sqrt-one-plus-x":
            case "series-l":
            case "taylor-asin":
            case "series-m":
            case "taylor-inv-sq":
            case "series-z":
            case "taylor-inv-cube":
            case "series-i":
            case "taylor-inv-one-plus-x2":
            case "series-y":
            case "origin-distance":
            case "rectangle-area":
            case "rectangle-perimeter":
            case "quadrant":
            case "ellipse-area":
            case "cylinder-volume":
            case "cone-volume":
            case "torus-volume":
                return 2;

            case "solve-quadratic":
            case "max3":
            case "triangle-area":
            case "triangle-valid":
            case "triangle-medians":
            case "triangle-bisectors":
            case "triangle-heights":
            case "triangle-angles":
                return 3;

            case "distance":
            case "manhattan-distance":
            case "midpoint":
            case "triangle-area-inradius":
            case "circle-segment":
                return 4;

            case "collinear":
            case "circle-line":
            case "circles-intersect":
            case "squares-intersect":
                return 6;

            case "rect-bounding-box":
                return 8;

            case "polygon":
                return VARIADIC;

            default:
                return UNKNOWN_COMMAND;
        }
    }

    // CORE-12: перевірка кількості аргументів
    public static boolean validateArgs(int actualCount, int expected) {
        if (expected == VARIADIC) {
            if (actualCount < VARIADIC_MIN_ARGS) {
                printError(ERR_NOT_ENOUGH_ARGS);
                return false;
            }
            return true;
        }
        if (actualCount < expected) {
            printError(ERR_NOT_ENOUGH_ARGS);
            return false;
        }
        if (actualCount > expected) {
            printError(ERR_TOO_MANY_ARGS);
            return false;
        }
        return true;
    }

    // CORE-13: парсинг усіх аргументів після назви команди
    public static Number[] parseArguments(String[] args) {
        Number[] result = new Number[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            Number value = parseNumber(args[i]);
            if (value == null) {
                printError(ERR_INVALID_NUMBER + args[i]);
                return null;
            }
            result[i - 1] = value;
        }
        return result;
    }

    // Long для цілих у межах long, Double для решти; null, якщо формат некоректний
    public static Number parseNumber(String raw) {
        String token = raw.trim();
        try {
            if (INTEGER_PATTERN.matcher(token).matches()) {
                try {
                    return Long.parseLong(token);
                } catch (NumberFormatException overflow) {
                    // ціле поза межами long -> парситься як double нижче
                }
            } else if (!DECIMAL_PATTERN.matcher(token).matches()) {
                return null;
            }
            double value = Double.parseDouble(token);
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Ціле значення аргументу (приймає 5, 5.0, 1e3); null, якщо число не ціле
    public static Long toLongExact(Number n) {
        if (n instanceof Long) {
            return (Long) n;
        }
        double v = n.doubleValue();
        if (v != Math.rint(v) || Math.abs(v) >= 0x1p63) {
            return null;
        }
        return (long) v;
    }

    public static double[] toDoubleArray(Number[] arguments) {
        double[] result = new double[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            result[i] = arguments[i].doubleValue();
        }
        return result;
    }

    // CORE-14: єдиний формат виводу
    public static void printResult(double value) {
        if (!Double.isFinite(value)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (value == 0.0) {
            value = 0.0; // -0.0 -> 0.0
        }
        System.out.printf(Locale.ROOT, "Result: %f%n", value);
    }

    public static void printResult(long value) {
        System.out.printf(Locale.ROOT, "Result: %d%n", value);
    }

    public static void printResult(boolean value) {
        System.out.printf(Locale.ROOT, "Result: %b%n", value);
    }

    public static void printResult(String value) {
        System.out.printf(Locale.ROOT, "Result: %s%n", value);
    }

    // CORE-15: єдина точка виводу помилок
    public static void printError(String message) {
        System.out.println(message);
    }

    public static void printUnknownCommand(String command) {
        printError(ERR_UNKNOWN_COMMAND + command);
        System.out.println(HINT_HELP);
    }

    // CORE-04 / CORE-16: довідка
    public static void printHelp() {
        System.out.println("JavaMathSolver v" + VERSION);
        System.out.println("Usage: java -cp src MathSolver <command> <arguments>");

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
        printHelpLine("help", "show this help");
    }

    private static void printHelpLine(String usage, String description) {
        System.out.printf(Locale.ROOT, "  %-42s %s%n", usage, description);
    }

    // ALGEBRA (ALG-01 ... ALG-06)
    public static double add(double a, double b) { return a + b; }
    public static double sub(double a, double b) { return a - b; }
    public static double mul(double a, double b) { return a * b; }
    public static double div(double a, double b) { return a / b; }
    public static double pow(double a, double b) { return Math.pow(a, b); }
    public static double abs(double a) { return Math.abs(a); }
    public static double sqrt(double a) { return Math.sqrt(a); }

    // --- Algebra Lab 2 ---
    public static void solveLinear(double a, double b) {
        if (a == 0.0) {
            if (b == 0.0) {
                printResult("Infinite solutions");
            } else {
                printResult("No solution");
            }
        } else {
            double x = -b / a;
            printResult(x);
        }
    }

    public static void solveQuadratic(double a, double b, double c) {
        if (a == 0.0) {
            solveLinear(b, c);
            return;
        }

        double d = b * b - 4.0 * a * c;

        if (d > 0.0) {
            double x1, x2;
            if (b >= 0.0) {
                x1 = (-b - Math.sqrt(d)) / (2.0 * a);
                x2 = (2.0 * c) / (-b - Math.sqrt(d));
            } else {
                x1 = (2.0 * c) / (-b + Math.sqrt(d));
                x2 = (-b + Math.sqrt(d)) / (2.0 * a);
            }
            printResult(String.format(Locale.ROOT, "x1=%f, x2=%f", Math.min(x1, x2), Math.max(x1, x2)));
        } else if (d == 0.0) {
            double x = -b / (2.0 * a);
            printResult(x);
        } else {
            printResult("No real roots");
        }
    }

    public static double max3(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    public static void solveGcd(long a, long b) {
        if (a == 0L && b == 0L) {
            printResult(0L);
            return;
        }
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger res = b1.gcd(b2);
        printResult(res.longValue());
    }

    public static void factorial(long n) {
        if (n < 0L || n > 20L) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        long result = 1L;
        for (long i = 2L; i <= n; i++) {
            result *= i;
        }
        printResult(result);
    }

    public static void fibonacci(long n) {
        if (n < 0L || n > 92L) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (n == 0L) {
            printResult(0L);
            return;
        }
        long prev = 0L;
        long curr = 1L;
        for (long i = 2L; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        printResult(curr);
    }

    private static double normalizeAngle(double x) {
        x = x % (2.0 * Math.PI);
        if (x > Math.PI) x -= 2.0 * Math.PI;
        if (x < -Math.PI) x += 2.0 * Math.PI;
        return x;
    }

    private static boolean validateInfiniteSeriesInputs(double x, double eps) {
        if (!Double.isFinite(x) || !Double.isFinite(eps) || eps <= 0.0) {
            printError(ERR_INVALID_INPUT);
            return false;
        }
        return true;
    }

    private static boolean validateSeriesInputs(double x, double eps) {
        if (!Double.isFinite(x) || !Double.isFinite(eps) || Math.abs(x) >= 1.0 || eps <= 0.0) {
            printError(ERR_INVALID_INPUT);
            return false;
        }
        return true;
    }

    public static void calculateTaylorSin(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        x = normalizeAngle(x);

        double sum = 0.0;
        double a = x;
        int step = 2;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * x / ((double) step * (step + 1));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            step += 2;
        }
        printResult(sum);
    }

    public static void calculateTaylorCos(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        x = normalizeAngle(x);

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * x / ((2.0 * k - 1.0) * (2.0 * k));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorSinh(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * (x * x) / ((2.0 * k) * (2.0 * k + 1.0));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorCosh(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * xSquared / ((2.0 * k - 1.0) * (2.0 * k));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorExp(double x, double eps) {
        if (!validateInfiniteSeriesInputs(x, eps)) return;

        boolean isNegative = x < 0.0;
        double absX = Math.abs(x);
        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * absX / k;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(isNegative ? (1.0 / sum) : sum);
    }

    public static void calculateTaylorLnOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * (k - 1.0) / k;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorGeomSeries(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorArtanh(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = a * xSquared * (2.0 * k - 3.0) / (2.0 * k - 1.0);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorSqrtOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * (2.0 * k - 3.0) / (2.0 * k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvSqrtOnePlusX(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * x * (2.0 * k - 1.0) / (2.0 * k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorAsin(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = x;
        int k = 1;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = a * xSquared * ((2.0 * k - 1.0) * (2.0 * k - 1.0)) / (2.0 * k * (2.0 * k + 1.0));
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
            k++;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvSq(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 0;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * ((k + 1.0) / k);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvCube(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        int k = 1;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            k++;
            a = -a * x * (k + 1.0) / (k - 1.0);
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    public static void calculateTaylorInvOnePlusX2(double x, double eps) {
        if (!validateSeriesInputs(x, eps)) return;

        double sum = 0.0;
        double a = 1.0;
        double xSquared = x * x;
        int iter = 0;

        while (Math.abs(a) >= eps && iter++ < MAX_SERIES_ITERATIONS) {
            sum += a;
            a = -a * xSquared;
            if (!Double.isFinite(a) || !Double.isFinite(sum) || a == 0.0) break;
        }
        printResult(sum);
    }

    // GEOMETRY (GEO-01 ... GEO-06)
    public static void calculateDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public static void calculateCircleArea(double r) {
        if (r < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(Math.PI * r * r);
    }

    public static void calculateCircleCircumference(double r) {
        if (r < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(2.0 * Math.PI * r);
    }

    public static void calculateRectangleArea(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(a * b);
    }

    public static void calculateRectanglePerimeter(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(2.0 * (a + b));
    }

    public static void calculateOriginDistance(double x, double y) {
        printResult(Math.sqrt(x * x + y * y));
    }

    // --- Geometry Lab 2 ---
    public static double heronArea(double sideA, double sideB, double sideC) {
        double m = Math.max(sideA, Math.max(sideB, sideC));
        if (m <= 0) {
            return 0;
        }
        double a = sideA / m;
        double b = sideB / m;
        double c = sideC / m;
        double s = (a + b + c) / 2.0;
        double diffA = s - a;
        double diffB = s - b;
        double diffC = s - c;
        if (diffA < 0 || diffB < 0 || diffC < 0) {
            return 0;
        }
        double scaledArea = Math.sqrt(s * diffA * diffB * diffC);
        return (scaledArea * m) * m;
    }

    public static void calculateTriangleArea(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double area = heronArea(sideA, sideB, sideC);
        if (Double.isInfinite(area) || Double.isNaN(area)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(area);
    }

    public static boolean triangleValid(double sideA, double sideB, double sideC) {
        return sideA > 0 && sideB > 0 && sideC > 0
                && (sideA + sideB > sideC)
                && (sideA + sideC > sideB)
                && (sideB + sideC > sideA);
    }

    public static void calculateTriangleValid(double sideA, double sideB, double sideC) {
        printResult(triangleValid(sideA, sideB, sideC));
    }

    public static void calculateQuadrant(double x, double y) {
        if (x == 0 && y == 0) {
            printResult("ORIGIN");
        } else if (x == 0 || y == 0) {
            printResult("AXIS");
        } else if (x > 0 && y > 0) {
            printResult("I");
        } else if (x < 0 && y > 0) {
            printResult("II");
        } else if (x < 0 && y < 0) {
            printResult("III");
        } else {
            printResult("IV");
        }
    }

    public static void calculateManhattanDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.abs(x2 - x1) + Math.abs(y2 - y1));
    }

    public static void calculateMidpoint(double x1, double y1, double x2, double y2) {
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        printResult(String.format(Locale.ROOT, "(%f, %f)", midX, midY));
    }

    public static void calculateCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        double crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        double len1 = Math.hypot(x2 - x1, y2 - y1);
        double len2 = Math.hypot(x3 - x1, y3 - y1);
        double len3 = Math.hypot(x3 - x2, y3 - y2);
        if (len1 == 0 || len2 == 0 || len3 == 0) {
            printResult(true);
            return;
        }
        double scale = len1 * len2;
        printResult(Math.abs(crossProduct) <= 1e-9 * scale);
    }


    // Task 7: Ellipse area
    public static void calculateEllipseArea(double radiusA, double radiusB) {
        if (radiusA < 0 || radiusB < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(Math.PI * radiusA * radiusB);
    }

    // Task 8a: Triangle medians
    public static void calculateMedians(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double medA = 0.5 * Math.sqrt(Math.max(0, 2 * sideB * sideB + 2 * sideC * sideC - sideA * sideA));
        double medB = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideC * sideC - sideB * sideB));
        double medC = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideB * sideB - sideC * sideC));
        if (Double.isInfinite(medA) || Double.isInfinite(medB) || Double.isInfinite(medC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "m_a=%f, m_b=%f, m_c=%f", medA, medB, medC));
    }

    // Task 8b: Triangle bisectors
    public static void calculateBisectors(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double bisA = Math.sqrt(Math.max(0, sideB * sideC * ((sideB + sideC) * (sideB + sideC) - sideA * sideA))) / (sideB + sideC);
        double bisB = Math.sqrt(Math.max(0, sideA * sideC * ((sideA + sideC) * (sideA + sideC) - sideB * sideB))) / (sideA + sideC);
        double bisC = Math.sqrt(Math.max(0, sideA * sideB * ((sideA + sideB) * (sideA + sideB) - sideC * sideC))) / (sideA + sideB);
        if (Double.isInfinite(bisA) || Double.isInfinite(bisB) || Double.isInfinite(bisC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "l_a=%f, l_b=%f, l_c=%f", bisA, bisB, bisC));
    }

    // Task 8c: Triangle heights
    public static void calculateHeights(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double area = heronArea(sideA, sideB, sideC);
        if (Double.isInfinite(area) || Double.isNaN(area)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double hA = 2 * area / sideA;
        double hB = 2 * area / sideB;
        double hC = 2 * area / sideC;
        if (Double.isInfinite(hA) || Double.isInfinite(hB) || Double.isInfinite(hC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(String.format(Locale.ROOT, "h_a=%f, h_b=%f, h_c=%f", hA, hB, hC));
    }

    // Task 9: Area by angles (in radians) and inradius
    public static void calculateAreaByAnglesAndInradius(double angleA, double angleB, double angleC, double inradius) {
        if (inradius <= 0 || angleA <= 0 || angleB <= 0 || angleC <= 0 || angleA >= Math.PI || angleB >= Math.PI || angleC >= Math.PI) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        if (Math.abs((angleA + angleB + angleC) - Math.PI) > 1e-4) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double sumCot = 1 / Math.tan(angleA / 2) + 1 / Math.tan(angleB / 2) + 1 / Math.tan(angleC / 2);
        double result = inradius * inradius * sumCot;
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(result);
    }

    // Task 10: Triangle angles (in radians and degrees)
    public static void calculateTriangleAngles(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double cosA = Math.min(1.0, Math.max(-1.0, (sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC)));
        double cosB = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC)));
        double cosC = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB)));

        double radA = Math.acos(cosA);
        double radB = Math.acos(cosB);
        double radC = Math.acos(cosC);

        printResult(String.format(Locale.ROOT, "A=%f rad (%f deg), B=%f rad (%f deg), C=%f rad (%f deg)",
                radA, Math.toDegrees(radA), radB, Math.toDegrees(radB), radC, Math.toDegrees(radC)));
    }

    // Task 11: Cylinder volume
    public static void calculateCylinderVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double vol = Math.PI * radius * radius * height;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }

    // Task 12: Cone volume
    public static void calculateConeVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double vol = Math.PI * radius * radius * height / 3;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }

    // Task 13: Torus volume
    public static void calculateTorusVolume(double innerRadius, double outerRadius) {
        if (innerRadius < 0 || outerRadius < 0 || innerRadius > outerRadius) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double tubeRadius = (outerRadius - innerRadius) / 2;
        double centerRadius = (outerRadius + innerRadius) / 2;
        double vol = 2 * Math.PI * Math.PI * centerRadius * tubeRadius * tubeRadius;
        if (Double.isInfinite(vol) || Double.isNaN(vol)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        printResult(vol);
    }

    // Task 14: Circle and segment intersection
    public static void calculateCircleSegmentIntersections(double radius, double lineX, double yMin, double lengthC) {
        if (radius < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double yMax = yMin + lengthC * lengthC;
        double discr = radius * radius - lineX * lineX;
        if (discr < -1e-9) {
            printResult(0);
            return;
        }
        if (Math.abs(discr) <= 1e-9) {
            int count = (0 >= yMin - 1e-9 && 0 <= yMax + 1e-9) ? 1 : 0;
            printResult(count);
            return;
        }
        double y = Math.sqrt(discr);
        int count = 0;
        if (y >= yMin - 1e-9 && y <= yMax + 1e-9) {
            count++;
        }
        if (-y >= yMin - 1e-9 && -y <= yMax + 1e-9) {
            count++;
        }
        printResult(count);
    }

    // Task 15: Circle and line intersection classification
    public static void calculateCircleLine(double centerX, double centerY, double radius, double lineA, double lineB, double lineC) {
        if (radius < 0 || (lineA == 0 && lineB == 0)) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double dist = Math.abs(lineA * centerX + lineB * centerY + lineC) / Math.hypot(lineA, lineB);
        if (Math.abs(dist - radius) < 1e-9) {
            printResult("One point of tangency");
        } else if (dist < radius) {
            printResult("Two points of intersection");
        } else {
            printResult("No common points");
        }
    }

    // Task 16: Intersection of two circles
    public static void calculateCirclesIntersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        if (r1 < 0 || r2 < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double dist = Math.hypot(x2 - x1, y2 - y1);
        boolean intersects = (dist <= r1 + r2 + 1e-9 && dist >= Math.abs(r1 - r2) - 1e-9);
        printResult(intersects);
    }

    // Task 17: Intersection of two squares
    public static void calculateSquaresIntersect(double x1, double y1, double side1, double x2, double y2, double side2) {
        if (side1 < 0 || side2 < 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double leftX = Math.max(x1, x2);
        double bottomY = Math.max(y1, y2);
        double rightX = Math.min(x1 + side1, x2 + side2);
        double topY = Math.min(y1 + side1, y2 + side2);
        if (leftX > rightX || bottomY > topY) {
            printResult("Squares do not intersect");
        } else {
            printResult(String.format(Locale.ROOT, "Intersect rect: Bottom-Left (%f, %f), Top-Right (%f, %f)", leftX, bottomY, rightX, topY));
        }
    }

    // Task 18: Minimum bounding box for two rectangles
    public static void calculateRectBoundingBox(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (x1 > x2 || y1 > y2 || x3 > x4 || y3 > y4) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        double minX = Math.min(x1, x3);
        double minY = Math.min(y1, y3);
        double maxX = Math.max(x2, x4);
        double maxY = Math.max(y2, y4);
        printResult(String.format(Locale.ROOT, "Bounding box: Bottom-Left (%f, %f), Top-Right (%f, %f)", minX, minY, maxX, maxY));
    }

    // Polygon task: perimeter and convexity
    public static void processPolygon(double... coords) {
        if (coords == null || coords.length < 6 || coords.length % 2 != 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        int count = coords.length / 2;
        double perimeter = 0;
        for (int i = 0; i < count; i++) {
            double x1 = coords[2 * i], y1 = coords[2 * i + 1];
            double x2 = coords[2 * ((i + 1) % count)], y2 = coords[2 * ((i + 1) % count) + 1];
            perimeter += Math.hypot(x2 - x1, y2 - y1);
        }

        int initialSign = 0;
        boolean signConsistent = true;
        double turningSum = 0;
        for (int i = 0; i < count; i++) {
            double x1 = coords[2 * i], y1 = coords[2 * i + 1];
            double x2 = coords[2 * ((i + 1) % count)], y2 = coords[2 * ((i + 1) % count) + 1];
            double x3 = coords[2 * ((i + 2) % count)], y3 = coords[2 * ((i + 2) % count) + 1];

            double edge1x = x2 - x1, edge1y = y2 - y1;
            double edge2x = x3 - x2, edge2y = y3 - y2;
            double crossProduct = edge1x * edge2y - edge1y * edge2x;
            double dotProduct = edge1x * edge2x + edge1y * edge2y;
            double len1 = Math.hypot(edge1x, edge1y);
            double len2 = Math.hypot(edge2x, edge2y);
            turningSum += Math.atan2(crossProduct, dotProduct);

            if (len1 * len2 > 0 && Math.abs(crossProduct) <= 1e-9 * (len1 * len2)) {
                continue;
            }
            int sign = crossProduct > 0 ? 1 : -1;
            if (initialSign == 0) {
                initialSign = sign;
            } else if (sign != initialSign) {
                signConsistent = false;
                break;
            }
        }
        boolean turningSumOk = Math.abs(Math.abs(turningSum) - 2 * Math.PI) < 1e-6;
        boolean isConvex = signConsistent && initialSign != 0 && turningSumOk;

        printResult(String.format(Locale.ROOT, "perimeter=%f, convex=%b", perimeter, isConvex));
    }

    // Monte Carlo simulation for triangle probability
    public static void calculateMonteCarloTriangle(int totalTrials) {
        calculateMonteCarloTriangle(totalTrials, 42L);
    }

    public static void calculateMonteCarloTriangle(int totalTrials, long seed) {
        if (totalTrials <= 0) {
            printError(ERR_INVALID_INPUT);
            return;
        }
        java.util.Random rnd = new java.util.Random(seed);
        int validCount = 0;
        for (int i = 0; i < totalTrials; i++) {
            double sideA = rnd.nextDouble(), sideB = rnd.nextDouble(), sideC = rnd.nextDouble();
            if (sideA + sideB > sideC && sideA + sideC > sideB && sideB + sideC > sideA) {
                validCount++;
            }
        }
        printResult((double) validCount / totalTrials);
    }

}