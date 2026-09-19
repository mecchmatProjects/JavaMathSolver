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

        // CORE-03: Перетворення текстових аргументів зі String у double
        double[] values = new double[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            try {
                values[i - 1] = Double.parseDouble(args[i]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number: " + args[i]);
                return;
            }
        }

        // CORE-11: Диспетчеризація на основі switch(command)
        switch (command) {
            case "help":
                printHelp();
                break;

            // --- ALGEBRA (Lab 1) ---
            case "add":
                printResult(add(values[0], values[1]));
                break;
            case "sub":
                printResult(sub(values[0], values[1]));
                break;
            case "mul":
                printResult(mul(values[0], values[1]));
                break;
            case "div":
                printResult(div(values[0], values[1]));
                break;
            case "pow":
                printResult(pow(values[0], values[1]));
                break;
            case "abs":
                printResult(abs(values[0]));
                break;
            case "sqrt":
                if (values[0] < 0) {
                    System.out.println("Error: square root of negative number");
                } else {
                    printResult(sqrt(values[0]));
                }
                break;

            // --- ALGEBRA (Lab 2 stubs) ---
            case "solve-linear":
                solveLinear(values[0], values[1]);
                break;
            case "solve-quadratic":
                solveQuadratic(values[0], values[1], values[2]);
                break;
            case "max3":
                max3(values[0], values[1], values[2]);
                break;
            case "gcd":
                gcd((int) values[0], (int) values[1]);
                break;
            case "factorial":
                factorial((int) values[0]);
                break;
            case "fibonacci":
                fibonacci((int) values[0]);
                break;

            // --- GEOMETRY (Lab 1) ---
            case "distance":
                calculateDistance(args);
                break;
            case "circle-area":
                calculateCircleArea(args);
                break;
            case "circle-circumference":
                calculateCircleCircumference(args);
                break;
            case "rectangle-area":
                calculateRectangleArea(args);
                break;
            case "rectangle-perimeter":
                calculateRectanglePerimeter(args);
                break;
            case "origin-distance":
                calculateOriginDistance(args);
                break;

            // --- GEOMETRY (Lab 2 stubs) ---
            case "triangle-area":
                calculateTriangleArea(args);
                break;
            case "triangle-valid":
                calculateTriangleValid(args);
                break;
            case "quadrant":
                calculateQuadrant(args);
                break;
            case "manhattan-distance":
                calculateManhattanDistance(args);
                break;
            case "midpoint":
                calculateMidpoint(args);
                break;
            case "collinear":
                calculateCollinear(args);
                break;

            // Завершення через default для невідомих команд
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Use 'help' to see available commands.");
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

    // Оновлений метод валідації (приймає вже визначену очікувану кількість)
    public static boolean validateArgs(int actualCount, int expected) {
        if (actualCount != expected) {
            System.out.println("Error: Wrong number of arguments");
            return false;
        }
        return true;
    }

    // CORE-14: Єдиний формат числового виводу.
    // Locale.ROOT потрібен, бо у локалях типу uk_UA %f друкує кому замість крапки
    public static void printResult(double value) {
        System.out.printf(Locale.ROOT, "Result: %f%n", value);
    }

    // CORE-04: Довідка
    public static void printHelp() {
        System.out.println("JavaMathSolver\n");
        System.out.println("Available commands:\n");

        // --- Algebra ---
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

        // --- Geometry ---
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
    public static double add(double a, double b) {
        return a + b;
    }

    public static double sub(double a, double b) {
        return a - b;
    }

    public static double mul(double a, double b) {
        return a * b;
    }

    public static double div(double a, double b) {
        return a / b;
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    public static double abs(double a) {
        return Math.abs(a);
    }

    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    // --- Algebra Lab 2 ---
    public static void solveLinear(double a, double b) {
        System.out.println("solve-linear is not implemented yet");
    }

    public static void solveQuadratic(double a, double b, double c) {
        System.out.println("solve-quadratic is not implemented yet");
    }

    public static void max3(double a, double b, double c) {
        System.out.println("max3 is not implemented yet");
    }

    public static void gcd(int a, int b) {
        System.out.println("gcd is not implemented yet");
    }

    public static void factorial(int n) {
        System.out.println("factorial is not implemented yet");
    }

    public static void fibonacci(int n) {
        System.out.println("fibonacci is not implemented yet");
    }

    // GEOMETRY (GEO-01 ... GEO-06)
    public static void calculateDistance(String[] args) {
        double x1 = Double.parseDouble(args[1]);
        double y1 = Double.parseDouble(args[2]);
        double x2 = Double.parseDouble(args[3]);
        double y2 = Double.parseDouble(args[4]);

        double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        printResult(d);
    }

    public static void calculateCircleArea(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double s = Math.PI * r * r;
        printResult(s);
    }

    public static void calculateCircleCircumference(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double c = 2 * Math.PI * r;
        printResult(c);
    }

    public static void calculateRectangleArea(String[] args) {
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a < 0 || b < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double s = a * b;
        printResult(s);
    }

    public static void calculateRectanglePerimeter(String[] args) {
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a < 0 || b < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double p = 2 * (a + b);
        printResult(p);
    }

    public static void calculateOriginDistance(String[] args) {
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);

        double d = Math.sqrt(x * x + y * y);
        printResult(d);
    }

    // --- Geometry Lab 2 Stubs ---
    public static void calculateTriangleArea(String[] args) {
        System.out.println("triangle-area is not implemented yet");
    }

    public static void calculateTriangleValid(String[] args) {
        System.out.println("triangle-valid is not implemented yet");
    }

    public static void calculateQuadrant(String[] args) {
        System.out.println("quadrant is not implemented yet");
    }

    public static void calculateManhattanDistance(String[] args) {
        System.out.println("manhattan-distance is not implemented yet");
    }

    public static void calculateMidpoint(String[] args) {
        System.out.println("midpoint is not implemented yet");
    }

    public static void calculateCollinear(String[] args) {
        System.out.println("collinear is not implemented yet");
    }
}