import java.util.Locale;

public class MathSolver {
    public static void main(String[] args) {
        // Базовий захист від виклику без параметрів
        if (args.length == 0) {
            System.out.println("JavaMathSolver v0.1");
            System.out.println("Use 'help' to see available commands.");
            return;
        }

        // CORE-01: Визначення назви операції з args[0]
        String command = args[0];

        // CORE-04: Обробка команди help
        if (command.equals("help")) {
            printHelp();
            return;
        }

        // CORE-12: Математична валідація кількості аргументів
        int expected = getExpectedArgsCount(command);

        // Якщо команда не знайдена у списку
        if (expected == -1) {
            System.out.println("Error: Unknown command: " + command);
            System.out.println("Use 'help' to see available commands.");
            return;
        }

        int actualArgsCount = args.length - 1;

        // Перевіряємо арність
        if (!validateArgs(actualArgsCount, expected)) {
            return;
        }

        // CORE-13: Парсинг в один масив окремо Long та Double
        Number[] arguments = new Number[args.length - 1];
        if (!parseArguments(arguments, args)) {
            return;
        }

        // CORE-11: Диспетчеризація на основі switch(command)
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
                if (arguments[1].doubleValue() == 0) {
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
                if (arguments[0].doubleValue() < 0) {
                    System.out.println("Error: Invalid mathematical input");
                } else {
                    printResult(sqrt(arguments[0].doubleValue()));
                }
                break;

            // --- ALGEBRA (Lab 2 stubs) ---
            case "solve-linear":
                solveLinear(arguments[0].doubleValue(), arguments[1].doubleValue());
                break;
            case "solve-quadratic":
                solveQuadratic(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue());
                break;
            case "max3":
                max3(arguments[0].doubleValue(), arguments[1].doubleValue(), arguments[2].doubleValue());
                break;
            case "gcd":
                gcd(arguments[0].intValue(), arguments[1].intValue());
                break;
            case "factorial":
                factorial(arguments[0].intValue());
                break;
            case "fibonacci":
                fibonacci(arguments[0].intValue());
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

    // CORE-12: Декомпозиція перевірки кількості аргументів
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

    // Заповнення масиву з числами
    public static boolean parseArguments(Number[] arr, String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            try {
                // Приводимо до нижнього регістру, щоб охопити як "e", так і "E"
                String argLower = args[i + 1].toLowerCase();
                if (!argLower.contains(".") && !argLower.contains("e")) {
                    arr[i] = Long.parseLong(args[i + 1]);
                } else {
                    arr[i] = Double.parseDouble(args[i + 1]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number: " + args[i + 1]);
                return false;
            }
        }
        return true;
    }

    // Оновлений метод валідації
    public static boolean validateArgs(int actualCount, int expected) {
        if (actualCount != expected) {
            System.out.println("Error: Wrong number of arguments");
            return false;
        }
        return true;
    }

    // CORE-14: Єдиний формат числового виводу.
    public static void printResult(double value) {
        System.out.printf(Locale.ROOT, "Result: %f%n", value);
    }

    // CORE-04: Довідка
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
        System.out.println("fibonacci n\n");
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
        if (a == 0) {
            if (b == 0) {
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
        // Якщо a == 0, рівняння вироджується в лінійне: bx + c = 0
        if (a == 0) {
            solveLinear(b, c);
            return;
        }

        double d = b * b - 4 * a * c;

        if (d > 0) {
            double x1 = (-b + Math.sqrt(d)) / (2 * a);
            double x2 = (-b - Math.sqrt(d)) / (2 * a);
            printResult(x1);
            printResult(x2);
        } else if (d == 0) {
            double x = -b / (2 * a);
            printResult(x);
        } else {
            System.out.println("No real roots");
        }
    }
    public static void max3(double a, double b, double c) { System.out.println("max3 is not implemented yet"); }
    public static void gcd(int a, int b) { System.out.println("gcd is not implemented yet"); }
    public static void factorial(int n) { System.out.println("factorial is not implemented yet"); }
    public static void fibonacci(int n) { System.out.println("fibonacci is not implemented yet"); }

    // GEOMETRY (GEO-01 ... GEO-06)
    public static void calculateDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public static void calculateCircleArea(double r) {
        if (r < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(Math.PI * r * r);
    }

    public static void calculateCircleCircumference(double r) {
        if (r < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(2 * Math.PI * r);
    }

    public static void calculateRectangleArea(double a, double b) {
        if (a < 0 || b < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(a * b);
    }

    public static void calculateRectanglePerimeter(double a, double b) {
        if (a < 0 || b < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(2 * (a + b));
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



    //algebra series problems(k,l,m)
    public static double calculateSeriesK(double x, double eps) {
        validateInputs(x, eps);
        double sum = 0.0;
        double a = 1.0; 
        int k = 1;
        while (Math.abs(a) >= eps) {
            sum += a;
            a = -a * x * (2.0 * k - 3) / (2.0 * k);
            k++;
        }
        return sum;
    }




    private static void validateSeriesProblemsInputs(double x, double eps) {
        if (Math.abs(x) >= 1.0) {
            throw new IllegalArgumentException("Argument |x| must be strictly less than 1. Received: " + x);
        }
        if (eps <= 0.0) {
            throw new IllegalArgumentException("Precision eps must be greater than 0. Received: " + eps);
        }
    }
    
}
