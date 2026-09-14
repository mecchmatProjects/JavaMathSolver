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
            System.out.println("  args[" + (i + 1) + "] = " + values[i]);
        }
    }
}