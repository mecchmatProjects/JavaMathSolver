import java.math.BigInteger;
import java.util.Locale;

public class MathSolver {

    private static final int MAX_SERIES_ITERATIONS = 10_000_000;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("JavaMathSolver v0.1");
            System.out.println("Use 'help' to see available commands.");
            return;
        }

        String command = args[0];

        if (command.equals("help")) {
            printHelp();
            return;
        }

        int expected = getExpectedArgsCount(command);
        if (expected == -1) {
            System.out.println("Error: Unknown command: " + command);
            System.out.println("Use 'help' to see available commands.");
            return;
        }

        int actualArgsCount = args.length - 1;
        if (!validateArgs(actualArgsCount, expected)) {
            return;
        }

        Number[] arguments = new Number[args.length - 1];
        if (!parseArguments(arguments, args)) {
            return;
        }

        switch (command) {
            // --- ALGEBRA (Lab 1) ---
            case "add":
                printResult(add(arguments[0].doubleValue(), arguments[1].doubleValue()));
                break;
            case "sub":
                printResult(sub(arguments[0].doubleValue(), arguments[1].doubleValue()));
                break;
            case "mul":
                printResult(mul(arguments[0].doubleValue(), arguments[1].doubleValue()));
                break;
            case "div":
                if (arguments[1].doubleValue() == 0.0) {
                    System.out.println("Error: Division by zero");
                } else {
                    printResult(div(arguments[0].doubleValue(), arguments[1].doubleValue()));
                }
                break;
            case "pow":
                printResult(pow(arguments[0].doubleValue(), arguments[1].doubleValue()));
                break;
            case "abs":
                printResult(abs(arguments[0].doubleValue()));
                break;
            case "sqrt":
                if (arguments[0].doubleValue() < 0.0) {
                    System.out.println("Error: Invalid mathematical input");
                } else {
                    printResult(sqrt(arguments[0].doubleValue()));
                }
                break;

            // --- ALGEBRA (Lab 2) ---
            case "solve-linear":
                solveLinear(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "solve-quadratic":
                solveQuadratic(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue());
                break;
            case "max3":
                printResult(max3(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue()));
                break;
            case "gcd":
                if (!(arguments[0] instanceof Long) || !(arguments[1] instanceof Long)) {
                    System.out.println("Error: Invalid mathematical input");
                } else {
                    solveGcd(arguments[0].longValue(), arguments[1].longValue());
                }
                break;
            case "factorial":
                if (!(arguments[0] instanceof Long)) {
                    System.out.println("Error: Invalid mathematical input");
                } else {
                    factorial(arguments[0].longValue());
                }
                break;
            case "fibonacci":
                if (!(arguments[0] instanceof Long)) {
                    System.out.println("Error: Invalid mathematical input");
                } else {
                    fibonacci(arguments[0].longValue());
                }
                break;

            // --- ALGEBRA (Taylor Series / Series) ---
            case "sin-taylor":
            case "taylor-sin":
                calculateTaylorSin(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-b":
            case "taylor-cos":
                calculateTaylorCos(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-c":
            case "taylor-sinh":
                calculateTaylorSinh(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-h":
            case "taylor-cosh":
                calculateTaylorCosh(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-e":
            case "taylor-exp":
                calculateTaylorExp(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-ln":
            case "taylor-ln-one-plus-x":
                calculateTaylorLnOnePlusX(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-ye":
            case "taylor-geom-series":
                calculateTaylorGeomSeries(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-j":
            case "taylor-artanh":
                calculateTaylorArtanh(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-k":
            case "taylor-sqrt-one-plus-x":
                calculateTaylorSqrtOnePlusX(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-l":
            case "taylor-inv-sqrt-one-plus-x":
                calculateTaylorInvSqrtOnePlusX(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-m":
            case "taylor-asin":
                calculateTaylorAsin(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-z":
            case "taylor-inv-sq":
                calculateTaylorInvSq(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-i":
            case "taylor-inv-cube":
                calculateTaylorInvCube(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "series-y":
            case "taylor-inv-one-plus-x2":
                calculateTaylorInvOnePlusX2(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;

            // --- GEOMETRY (Lab 1) ---
            case "distance":
                calculateDistance(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue(), arguments[3].doubleValue());
                break;
            case "circle-area":
                calculateCircleArea(arguments[0].doubleValue());
                break;
            case "circle-circumference":
                calculateCircleCircumference(arguments[0].doubleValue());
                break;
            case "rectangle-area":
                calculateRectangleArea(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "rectangle-perimeter":
                calculateRectanglePerimeter(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "origin-distance":
                calculateOriginDistance(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;

            // --- GEOMETRY (Lab 2 stubs) ---
            case "triangle-area":
                calculateTriangleArea(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue());
                break;
            case "triangle-valid":
                calculateTriangleValid(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue());
                break;
            case "quadrant":
                calculateQuadrant(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "manhattan-distance":
                calculateManhattanDistance(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue(), arguments[3].doubleValue());
                break;
            case "midpoint":
                calculateMidpoint(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue(), arguments[3].doubleValue());
                break;
            case "collinear":
                calculateCollinear(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue(), arguments[3].doubleValue(), arguments[4].doubleValue(), arguments[5].doubleValue());
                break;
        }
    }

    public static int getExpectedArgsCount(String command) {
        switch (command) {
            case "abs":
            case "sqrt":
            case "circle-area":
            case "circle-circumference":
            case "factorial":
            case "fibonacci":
                return 1;

            case "add":
            case "sub":
            case "mul":
            case "div":
            case "pow":
            case "origin-distance":
            case "rectangle-area":
            case "rectangle-perimeter":
            case "solve-linear":
            case "gcd":
            case "quadrant":
            case "sin-taylor":
            case "taylor-sin":
            case "series-b":
            case "taylor-cos":
            case "series-c":
            case "taylor-sinh":
            case "series-h":
            case "taylor-cosh":
            case "series-e":
            case "taylor-exp":
            case "series-ln":
            case "taylor-ln-one-plus-x":
            case "series-ye":
            case "taylor-geom-series":
            case "series-j":
            case "taylor-artanh":
            case "series-k":
            case "taylor-sqrt-one-plus-x":
            case "series-l":
            case "taylor-inv-sqrt-one-plus-x":
            case "series-m":
            case "taylor-asin":
            case "series-z":
            case "taylor-inv-sq":
            case "series-i":
            case "taylor-inv-cube":
            case "series-y":
            case "taylor-inv-one-plus-x2":
                return 2;

            case "solve-quadratic":
            case "max3":
            case "triangle-area":
            case "triangle-valid":
                return 3;

            case "distance":
            case "manhattan-distance":
            case "midpoint":
                return 4;

            case "collinear":
                return 6;

            default:
                return -1;
        }
    }

    public static boolean parseArguments(Number[] arr, String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            try {
                String token = args[i + 1].trim();
                String argLower = token.toLowerCase();

                if (!argLower.contains(".") && !argLower.contains("e")) {
                    arr[i] = Long.parseLong(token);
                } else {
                    double val = Double.parseDouble(token);
                    if (Double.isNaN(val) || Double.isInfinite(val)) {
                        System.out.println("Error: Invalid mathematical input");
                        return false;
                    }
                    arr[i] = val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number: " + args[i + 1]);
                return false;
            }
        }
        return true;
    }

    public static boolean validateArgs(int actualCount, int expected) {
        if (actualCount != expected) {
            System.out.println("Error: Wrong number of arguments");
            return false;
        }
        return true;
    }

    public static void printResult(double value) {
        if (value == 0.0 || Double.doubleToRawLongBits(value) == 0x8000000000000000L) {
            value = 0.0;
        }
        System.out.printf(Locale.ROOT, "Result: %f%n", value);
    }

    public static void printResult(long value) {
        System.out.printf(Locale.ROOT, "Result: %d%n", value);
    }

    public static void printHelp() {
        System.out.println("JavaMathSolver\n");
        System.out.println("Available commands:\n");
        System.out.println("add a b");
        System.out.println("sub a b");
        System.out.println("mul a b");
        System.out.println("div a b");
        System.out.println("pow a b");
        System.out.println("sqrt x");
        System.out.println("abs x");
        System.out.println("solve-linear a b");
        System.out.println("solve-quadratic a b c");
        System.out.println("max3 a b c");
        System.out.println("gcd a b");
        System.out.println("factorial n");
        System.out.println("fibonacci n");
        System.out.println("sin-taylor x eps");
        System.out.println("series-b x eps");
        System.out.println("series-c x eps");
        System.out.println("series-h x eps");
        System.out.println("series-e x eps");
        System.out.println("series-ln x eps");
        System.out.println("series-ye x eps");
        System.out.println("series-j x eps");
        System.out.println("series-k x eps");
        System.out.println("series-l x eps");
        System.out.println("series-m x eps");
        System.out.println("series-z x eps");
        System.out.println("series-i x eps");
        System.out.println("series-y x eps\n");
        System.out.println("distance x1 y1 x2 y2");
        System.out.println("circle-area r");
        System.out.println("circle-circumference r");
        System.out.println("rectangle-area a b");
        System.out.println("rectangle-perimeter a b");
        System.out.println("origin-distance x y");
        System.out.println("triangle-area a b c");
        System.out.println("triangle-valid a b c");
        System.out.println("quadrant x y");
        System.out.println("manhattan-distance x1 y1 x2 y2");
        System.out.println("midpoint x1 y1 x2 y2");
        System.out.println("collinear x1 y1 x2 y2 x3 y3");
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
                System.out.println("Infinite solutions");
            } else {
                System.out.println("No solution");
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
            printResult(Math.min(x1, x2));
            printResult(Math.max(x1, x2));
        } else if (d == 0.0) {
            double x = -b / (2.0 * a);
            printResult(x);
        } else {
            System.out.println("No real roots");
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
        System.out.printf(Locale.ROOT, "Result: %s%n", res.toString());
    }

    public static void factorial(long n) {
        if (n < 0L || n > 20L) {
            System.out.println("Error: Invalid mathematical input");
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
            System.out.println("Error: Invalid mathematical input");
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
            System.out.println("Error: Invalid mathematical input");
            return false;
        }
        return true;
    }

    private static boolean validateSeriesInputs(double x, double eps) {
        if (!Double.isFinite(x) || !Double.isFinite(eps) || Math.abs(x) >= 1.0 || eps <= 0.0) {
            System.out.println("Error: Invalid mathematical input");
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
        double a = 2.0 * x;
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
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(Math.PI * r * r);
    }

    public static void calculateCircleCircumference(double r) {
        if (r < 0.0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(2.0 * Math.PI * r);
    }

    public static void calculateRectangleArea(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(a * b);
    }

    public static void calculateRectanglePerimeter(double a, double b) {
        if (a < 0.0 || b < 0.0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(2.0 * (a + b));
    }

    public static void calculateOriginDistance(double x, double y) {
        printResult(Math.sqrt(x * x + y * y));
    }

    // --- Geometry Lab 2 Stubs ---
    public static void calculateTriangleArea(double a, double b, double c) { System.out.println("triangle-area is not implemented yet"); }
    public static void calculateTriangleValid(double a, double b, double c) { System.out.println("triangle-valid is not implemented yet"); }
    public static void calculateQuadrant(double x, double y) { System.out.println("quadrant is not implemented yet"); }
    public static void calculateManhattanDistance(double x1, double y1, double x2, double y2) { System.out.println("manhattan-distance is not implemented yet"); }
    public static void calculateMidpoint(double x1, double y1, double x2, double y2) { System.out.println("midpoint is not implemented yet"); }
    public static void calculateCollinear(double x1, double y1, double x2, double y2, double x3, double y3) { System.out.println("collinear is not implemented yet"); }
}
