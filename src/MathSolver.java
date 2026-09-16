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

        boolean known = 
            command.equals("add") ||
            command.equals("sub") ||
            command.equals("mul") ||
            command.equals("div") ||
            command.equals("pow") ||
            command.equals("sqrt") ||
            command.equals("abs") ||
            command.equals("distance") ||
            command.equals("circle-area") ||
            command.equals("circle-circumference") ||
            command.equals("rectangle-area") ||
            command.equals("rectangle-perimeter") ||
            command.equals("origin-distance") ||
            command.equals("help");

        if (!known) {
            System.out.println("Unknown command: " + command);
            System.out.println("Use 'help' to see available commands.");
            return;
        }

        System.out.println("Command received: " + command);

        // CORE-02: Перевірка потрібної кількості аргументів
        if (command.equals("add") ||
            command.equals("sub") ||
            command.equals("mul") ||
            command.equals("div") ||
            command.equals("pow") ||
            command.equals("origin-distance") ||
            command.equals("rectangle-area") ||
            command.equals("rectangle-perimeter")) {
            if (args.length < 3) {
                System.out.println("Error: Not enough arguments");
                System.out.println("Use 2 numbers!");
                return;
            } else if (args.length > 3) {
                System.out.println("Error: Too many arguments");
                System.out.println("Use only 2 numbers!");
                return;
            }
        }

        if (command.equals("abs") ||
            command.equals("sqrt") ||
            command.equals("circle-area") ||
            command.equals("circle-circumference")) {
            if (args.length < 2) {
                System.out.println("Error: Not enough arguments");
                System.out.println("Use a number!");
                return;
            } else if (args.length > 2) {
                System.out.println("Error: Too many arguments");
                System.out.println("Use only one number!");
                return;
            }
        }

        if (command.equals("distance")) {
            if (args.length < 5) {
                System.out.println("Error: Not enough arguments");
                System.out.println("Use 4 numbers!");
                return;
            } else if (args.length > 5) {
                System.out.println("Error: Too many arguments");
                System.out.println("Use only 4 numbers!");
                return;
            }
        }

        // CORE-03: Перетворення текстових аргументів зі String у double.
        // Команда help не приймає числових аргументів, тому її не парсимо.
        if (command.equals("help")) {
            return;
        }

        double[] values = new double[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            try {
                values[i - 1] = Double.parseDouble(args[i]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + args[i]);
                System.out.println("Arguments must be numeric, for example: 10, -5, 3.14");
                return;
            }
        }

        System.out.println("Parsed " + values.length + " numeric argument(s):");
        for (int i = 0; i < values.length; i++) {
            System.out.println(" args[" + (i + 1) + "] = " + values[i]);
        }
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
        System.out.println("abs x\n");
        System.out.println("distance x1 y1 x2 y2");
        System.out.println("circle-area r");
        System.out.println("circle-circumference r");
        System.out.println("rectangle-area a b");
        System.out.println("rectangle-perimeter a b");
        System.out.println("origin-distance x y");
    }

    // GEO-01: Distance between points
    public static void calculateDistance(String[] args) {
        double x1 = Double.parseDouble(args[1]);
        double y1 = Double.parseDouble(args[2]);
        double x2 = Double.parseDouble(args[3]);
        double y2 = Double.parseDouble(args[4]);

        double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        System.out.println("distance = " + d);
    }

    // GEO-02: Circle area
    public static void calculateCircleArea(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: radius cannot be negative");
            return;
        }
        double s = Math.PI * r * r;
        System.out.println("circle area = " + s);
    }

    // GEO-03: Circle circumference
    public static void calculateCircleCircumference(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: radius cannot be negative");
            return;
        }
        double c = 2 * Math.PI * r;
        System.out.println("circle circumference = " + c);
    }

    // GEO-04: Rectangle area
    public static void calculateRectangleArea(String[] args) {
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a < 0 || b < 0) {
            System.out.println("Error: side lengths cannot be negative");
            return;
        }
        double s = a * b;
        System.out.println("rectangle area = " + s);
    }

    // GEO-05: Rectangle perimeter
    public static void calculateRectanglePerimeter(String[] args) {
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a < 0 || b < 0) {
            System.out.println("Error: side lengths cannot be negative");
            return;
        }
        double p = 2 * (a + b);
        System.out.println("rectangle perimeter = " + p);
    }

    // GEO-06: Euclidean distance from origin
    public static void calculateOriginDistance(String[] args) {
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);

        double d = Math.sqrt(x * x + y * y);
        System.out.println("origin distance = " + d);
    }
}