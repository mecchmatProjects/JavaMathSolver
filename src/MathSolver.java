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
        System.out.println("Command received: " + command);

        // CORE-02 перевірка потрібної кількості аргументів
        if(command.equals("add") || command.equals("sub") || command.equals("mul") || command.equals("div") || command.equals("pow")
                || command.equals("origin-distance") || command.equals("rectangle-area") || command.equals("rectangle-perimeter")){
            if(args.length<3) {
                System.out.println("Error: Not enough arguments");
                System.out.println("Use 2 numbers!");
                return;
            }
            else if(args.length>3){
                System.out.println("Error: Too many arguments");
                System.out.println("Use only 2 numbers!");
                return;
            }
        }
        if(command.equals("abs") || command.equals("sqrt") || command.equals("circle-area") || command.equals("circle-circumference")){
            if(args.length<2){
                System.out.println("Error: Not enough arguments");
                System.out.println("Use a number!");
                return;
            }
            else if(args.length>2){
                System.out.println("Error: Too many arguments");
                System.out.println("Use only one number!");
                return;
            }
        }
        if(command.equals("distance")){
            if(args.length<5) {
                System.out.println("Error: Not enough arguments");
                System.out.println("Use 4 numbers!");
                return;
            }
            else if(args.length>5){
                System.out.println("Error: Too many arguments");
                System.out.println("Use only 4 numbers!");
                return;
            }
        }
    }
}