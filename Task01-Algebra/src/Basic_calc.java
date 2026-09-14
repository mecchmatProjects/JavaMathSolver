import java.util.Scanner;

public class Basic_calc {
    public static double add(double a, double b){
        return a + b;
    }

    public static double sub(double a, double b){
        return a - b;
    }

    public static double mult(double a, double b){
        return a * b;
    }

    //TODO: перевірити поведінку різних типів
    public static double div(double a, double b){
        return a / b;
    }

    public static double pow(double a, double b){
        return Math.pow(a, b);
    }

    //TODO: test
    public static double abs(double a){
        return Math.abs(a);
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Введіть операцію (add, sub, mul, div, pow, abs або exit): ");
            String op = scanner.next();

            // check if client wants to exit
            if (op.equals("exit")) {
                System.out.println("Бувай!");
                break;
            }

            // we need 2 nums
            if (op.equals("add")) {
                System.out.print("Введіть 2 числа: ");
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                System.out.println(Basic_calc.add(a, b));

            } else if (op.equals("sub")) {
                System.out.print("Введіть 2 числа: ");
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                System.out.println(Basic_calc.sub(a, b));

            } else if (op.equals("mul")) {
                System.out.print("Введіть 2 числа: ");
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                System.out.println(Basic_calc.mult(a, b));

            } else if (op.equals("div")) {
                System.out.print("Введіть 2 числа: ");
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                System.out.println(Basic_calc.div(a, b));

            } else if (op.equals("pow")) {
                System.out.print("Введіть 2 числа: ");
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                System.out.println(Basic_calc.pow(a, b));

                // we need 1 num
            } else if (op.equals("abs")) {
                System.out.print("Введіть 1 число: ");
                double x = scanner.nextDouble();
                System.out.println(Basic_calc.abs(x));

            } else {
                System.out.println("Ой! Невідома операція!");
            }
        }

        scanner.close();


    }

}
