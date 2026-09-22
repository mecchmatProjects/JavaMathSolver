import java.util.*;

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
    public static void solveLinear(double a, double b) { System.out.println("solve-linear is not implemented yet"); }
    public static void solveQuadratic(double a, double b, double c) { System.out.println("solve-quadratic is not implemented yet"); }
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

    // --- Geometry Lab 2 ---
    public static void calculateTriangleArea(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            System.out.println("Error: Invalid triangle sides");
            return;
        }
        double semiPerim = (sideA + sideB + sideC) / 2;
        double area = Math.sqrt(Math.max(0, semiPerim * (semiPerim - sideA) * (semiPerim - sideB) * (semiPerim - sideC)));
        printResult(area);
    }
    
    public static boolean triangleValid(double sideA, double sideB, double sideC) {
        return sideA > 0 && sideB > 0 && sideC > 0
            && (sideA + sideB > sideC)
            && (sideA + sideC > sideB)
            && (sideB + sideC > sideA);
    }
    
    public static void calculateTriangleValid(double sideA, double sideB, double sideC) {
        System.out.println(triangleValid(sideA, sideB, sideC));
    }
    
    public static void calculateQuadrant(double x, double y) {
        if (x == 0 && y == 0) {
            System.out.println("ORIGIN");
        } else if (x == 0 || y == 0) {
            System.out.println("AXIS");
        } else if (x > 0 && y > 0) {
            System.out.println("I");
        } else if (x < 0 && y > 0) {
            System.out.println("II");
        } else if (x < 0 && y < 0) {
            System.out.println("III");
        } else {
            System.out.println("IV");
        }
    }
    
    public static void calculateManhattanDistance(double x1, double y1, double x2, double y2) {
        printResult(Math.abs(x2 - x1) + Math.abs(y2 - y1));
    }
    
    public static void calculateMidpoint(double x1, double y1, double x2, double y2) {
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        System.out.printf(Locale.ROOT, "Midpoint: (%f, %f)%n", midX, midY);
    }
    
    public static void calculateCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        double crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        System.out.println(Math.abs(crossProduct) < 1e-9);
    }
    
    public static void calculateEllipseArea(double radiusA, double radiusB) {
        if (radiusA < 0 || radiusB < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(Math.PI * radiusA * radiusB);
    }

    // Task 8a: Triangle medians
    public static void calculateMedians(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            System.out.println("Error: Invalid triangle sides");
            return;
        }
        double medA = 0.5 * Math.sqrt(Math.max(0, 2 * sideB * sideB + 2 * sideC * sideC - sideA * sideA));
        double medB = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideC * sideC - sideB * sideB));
        double medC = 0.5 * Math.sqrt(Math.max(0, 2 * sideA * sideA + 2 * sideB * sideB - sideC * sideC));
        System.out.printf(Locale.ROOT, "Medians: m_a=%f, m_b=%f, m_c=%f%n", medA, medB, medC);
    }
    
    // Task 8b: Triangle bisectors
    public static void calculateBisectors(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            System.out.println("Error: Invalid triangle sides");
            return;
        }
        double bisA = Math.sqrt(Math.max(0, sideB * sideC * ((sideB + sideC) * (sideB + sideC) - sideA * sideA))) / (sideB + sideC);
        double bisB = Math.sqrt(Math.max(0, sideA * sideC * ((sideA + sideC) * (sideA + sideC) - sideB * sideB))) / (sideA + sideC);
        double bisC = Math.sqrt(Math.max(0, sideA * sideB * ((sideA + sideB) * (sideA + sideB) - sideC * sideC))) / (sideA + sideB);
        System.out.printf(Locale.ROOT, "Bisectors: l_a=%f, l_b=%f, l_c=%f%n", bisA, bisB, bisC);
    }
    
    // Task 8c: Triangle heights
    public static void calculateHeights(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            System.out.println("Error: Invalid triangle sides");
            return;
        }
        double semiPerim = (sideA + sideB + sideC) / 2;
        double area = Math.sqrt(Math.max(0, semiPerim * (semiPerim - sideA) * (semiPerim - sideB) * (semiPerim - sideC)));
        double hA = 2 * area / sideA;
        double hB = 2 * area / sideB;
        double hC = 2 * area / sideC;
        System.out.printf(Locale.ROOT, "Heights: h_a=%f, h_b=%f, h_c=%f%n", hA, hB, hC);
    }
    
    // Task 9: Area by angles (in radians) and inradius
    public static void calculateAreaByAnglesAndInradius(double angleA, double angleB, double angleC, double inradius) {
        if (inradius <= 0 || angleA <= 0 || angleB <= 0 || angleC <= 0 || angleA >= Math.PI || angleB >= Math.PI || angleC >= Math.PI) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        if (Math.abs((angleA + angleB + angleC) - Math.PI) > 1e-4) {
            System.out.println("Error: Sum of angles must be equal to PI radians");
            return;
        }
        double sumCot = 1 / Math.tan(angleA / 2) + 1 / Math.tan(angleB / 2) + 1 / Math.tan(angleC / 2);
        printResult(inradius * inradius * sumCot);
    }
    
    // Task 10: Triangle angles (in radians and degrees)
    public static void calculateTriangleAngles(double sideA, double sideB, double sideC) {
        if (!triangleValid(sideA, sideB, sideC)) {
            System.out.println("Error: Invalid triangle sides");
            return;
        }
        double cosA = Math.min(1.0, Math.max(-1.0, (sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC)));
        double cosB = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC)));
        double cosC = Math.min(1.0, Math.max(-1.0, (sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB)));
    
        double radA = Math.acos(cosA);
        double radB = Math.acos(cosB);
        double radC = Math.acos(cosC);
    
        System.out.printf(Locale.ROOT, "Angles (rad): A=%f, B=%f, C=%f%n", radA, radB, radC);
        System.out.printf(Locale.ROOT, "Angles (deg): A=%f, B=%f, C=%f%n", Math.toDegrees(radA), Math.toDegrees(radB), Math.toDegrees(radC));
    }

    // Task 11: Cylinder volume
    public static void calculateCylinderVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(Math.PI * radius * radius * height);
    }

    // Task 12: Cone volume
    public static void calculateConeVolume(double radius, double height) {
        if (radius < 0 || height < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        printResult(Math.PI * radius * radius * height / 3);
    }

    // Task 13: Torus volume
    public static void calculateTorusVolume(double innerRadius, double outerRadius) {
        if (innerRadius < 0 || outerRadius < 0 || innerRadius > outerRadius) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double tubeRadius = (outerRadius - innerRadius) / 2;
        double centerRadius = (outerRadius + innerRadius) / 2;
        printResult(2 * Math.PI * Math.PI * centerRadius * tubeRadius * tubeRadius);
    }

    // Task 14: Circle and segment intersection
    public static void calculateCircleSegmentIntersections(double radius, double lineX, double yMin, double lengthC) {
        if (radius < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double yMax = yMin + lengthC * lengthC;
        double discr = radius * radius - lineX * lineX;
        if (discr < -1e-9) {
            System.out.println("Intersections: 0");
            return;
        }
        if (Math.abs(discr) <= 1e-9) {
            int count = (0 >= yMin - 1e-9 && 0 <= yMax + 1e-9) ? 1 : 0;
            System.out.println("Intersections: " + count);
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
        System.out.println("Intersections: " + count);
    }

    // Task 15: Circle and line intersection classification
    public static void calculateCircleLine(double centerX, double centerY, double radius, double lineA, double lineB, double lineC) {
        if (radius < 0 || (lineA == 0 && lineB == 0)) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double dist = Math.abs(lineA * centerX + lineB * centerY + lineC) / Math.hypot(lineA, lineB);
        if (Math.abs(dist - radius) < 1e-9) {
            System.out.println("One point of tangency");
        } else if (dist < radius) {
            System.out.println("Two points of intersection");
        } else {
            System.out.println("No common points");
        }
    }
    
    // Task 16: Intersection of two circles
    public static void calculateCirclesIntersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        if (r1 < 0 || r2 < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double dist = Math.hypot(x2 - x1, y2 - y1);
        boolean intersects = (dist <= r1 + r2 + 1e-9 && dist >= Math.abs(r1 - r2) - 1e-9);
        System.out.println("Intersects: " + intersects);
    }

    // Task 17: Intersection of two squares
    public static void calculateSquaresIntersect(double x1, double y1, double side1, double x2, double y2, double side2) {
        if (side1 < 0 || side2 < 0) {
            System.out.println("Error: Invalid mathematical input");
            return;
        }
        double leftX = Math.max(x1, x2);
        double bottomY = Math.max(y1, y2);
        double rightX = Math.min(x1 + side1, x2 + side2);
        double topY = Math.min(y1 + side1, y2 + side2);
        if (leftX > rightX || bottomY > topY) {
            System.out.println("Squares do not intersect");
        } else {
            System.out.printf(Locale.ROOT, "Intersect rect: Bottom-Left (%f, %f), Top-Right (%f, %f)%n", leftX, bottomY, rightX, topY);
        }
    }

    // Task 18: Minimum bounding box for two rectangles
    public static void calculateRectBoundingBox(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (x1 > x2 || y1 > y2 || x3 > x4 || y3 > y4) {
            System.out.println("Error: Bottom-left coordinates must be <= top-right coordinates");
            return;
        }
        double minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
        double minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        double maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        double maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        System.out.printf(Locale.ROOT, "Bounding box: Bottom-Left (%f, %f), Top-Right (%f, %f)%n", minX, minY, maxX, maxY);
    }

    public static void processPolygon() {
        Scanner scanner = new Scanner(System.in);
        List<double[]> points = new ArrayList<>();
        while (scanner.hasNext()) {
            String strX = scanner.next();
            if (strX.equalsIgnoreCase("null")) {
                if (scanner.hasNext()) {
                    scanner.next();
                }
                break;
            }
            if (!scanner.hasNext()) {
                break;
            }
            String strY = scanner.next();
            if (strY.equalsIgnoreCase("null")) {
                break;
            }
            
            points.add(new double[]{Double.parseDouble(strX), Double.parseDouble(strY)});
        }
        
        if (points.size() < 3) {
            System.out.println("Error: A polygon must have at least 3 vertices.");
            return;
        }
        
        double perimeter = 0;
        int count = points.size();
        for (int i = 0; i < count; i++) {
            double[] p1 = points.get(i), p2 = points.get((i + 1) % count);
            perimeter += Math.hypot(p2[0] - p1[0], p2[1] - p1[1]);
        }

        int initialSign = 0;
        boolean isConvex = true;
        for (int i = 0; i < count; i++) {
            double[] p1 = points.get(i), p2 = points.get((i + 1) % count), p3 = points.get((i + 2) % count);
            double crossProduct = (p2[0] - p1[0]) * (p3[1] - p2[1]) - (p2[1] - p1[1]) * (p3[0] - p2[0]);
            if (Math.abs(crossProduct) < 1e-9) {
                continue;
            }
            int sign = crossProduct > 0 ? 1 : -1;
            if (initialSign == 0) {
                initialSign = sign;
            } else if (sign != initialSign) {
                isConvex = false;
                break;
            }
        }
        if (initialSign == 0) {
            isConvex = false;
        }

        System.out.printf(Locale.ROOT, "Perimeter: %f%n", perimeter);
        System.out.println("Is convex: " + isConvex);
    }

    // Monte Carlo simulation for triangle probability
    public static void calculateMonteCarloTriangle(int totalTrials) {
        if (totalTrials <= 0) {
            System.out.println("Error: Number of trials must be positive");
            return;
        }
        int validCount = 0;
        for (int i = 0; i < totalTrials; i++) {
            double sideA = Math.random(), sideB = Math.random(), sideC = Math.random();
            if (sideA + sideB > sideC && sideA + sideC > sideB && sideB + sideC > sideA) {
                validCount++;
            }
        }
        printResult((double) validCount / totalTrials);
    }

}
