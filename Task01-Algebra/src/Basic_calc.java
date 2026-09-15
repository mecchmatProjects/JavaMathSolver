import java.util.Scanner;
import java.util.InputMismatchException;

public class Basic_calc {
    public static double add(double a, double b) {
        return a + b;
    }

    public static double sub(double a, double b) {
        return a - b;
    }

    public static double mul(double a, double b) {
        return a * b;
    }

    public static double divDouble(double a, double b) {
        return a / b;
    }

    // ALG-04 — Цілочисельне ділення (int)
    public static int divInt(int a, int b) {
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Введіть операцію (add, sub, mul, div, pow, abs, sqrt або exit): ");
            String op = scanner.next();

            if (op.equals("exit")) {
                System.out.println("Бувай!");
                break;
            }

            if (op.equals("add")) {
                System.out.print("Введіть 2 числа: ");

                try {
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 2 числа.");
                    } else {
                        System.out.println(Basic_calc.add(a, b));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else if (op.equals("sub")) {
                System.out.print("Введіть 2 числа: ");

                try {
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 2 числа.");
                    } else {
                        System.out.println(Basic_calc.sub(a, b));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else if (op.equals("mul")) {
                System.out.print("Введіть 2 числа: ");

                try {
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 2 числа.");
                    } else {
                        System.out.println(Basic_calc.mul(a, b));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else if (op.equals("div")) {
                System.out.print("Введіть 2 числа: ");

                String inputA = scanner.next();
                String inputB = scanner.next();

                if (!scanner.nextLine().trim().isEmpty()) {
                    System.out.println("Помилка: потрібно ввести тільки 2 числа.");
                } else {

                    boolean isFloatingPoint =
                            inputA.contains(".") || inputA.contains(",")
                                    || inputB.contains(".") || inputB.contains(",");

                    if (isFloatingPoint) {
                        double a = Double.parseDouble(inputA.replace(',', '.'));
                        double b = Double.parseDouble(inputB.replace(',', '.'));

                        System.out.println("Дійсний результат: " + divDouble(a, b));

                    } else {
                        int a = Integer.parseInt(inputA);
                        int b = Integer.parseInt(inputB);

                        if (b == 0) {
                            System.out.println("Помилка: ділення на цілочисельний нуль неможливе!");
                        } else {
                            System.out.println("Цілочисельний результат: " + divInt(a, b));
                        }
                    }
                }

            } else if (op.equals("pow")) {
                System.out.print("Введіть 2 числа: ");

                try {
                    double a = scanner.nextDouble();
                    double b = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 2 числа.");
                    } else {
                        System.out.println(Basic_calc.pow(a, b));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else if (op.equals("abs")) {
                System.out.print("Введіть 1 число: ");

                try {
                    double x = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 1 число.");
                    } else {
                        System.out.println(Basic_calc.abs(x));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else if (op.equals("sqrt")) {
                System.out.print("Введіть 1 число: ");

                try {
                    double x = scanner.nextDouble();

                    if (!scanner.nextLine().trim().isEmpty()) {
                        System.out.println("Помилка: потрібно ввести тільки 1 число.");
                    } else {
                        System.out.println(Basic_calc.sqrt(x));
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid number");
                    scanner.nextLine();
                }

            } else {
                System.out.println("Ой! Невідома операція!");
            }
        }

        scanner.close();
    }
}