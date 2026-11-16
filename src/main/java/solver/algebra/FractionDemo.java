package solver.algebra;

public class FractionDemo {
    public static void main(String[] args) {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);

        System.out.println("Перший дріб: " + a);
        System.out.println("Другий дріб: " + b);

        System.out.println("Додавання: " + a.add(b));
        System.out.println("Віднімання: " + a.subtract(b));
        System.out.println("Множення: " + a.multiply(b));
        System.out.println("Ділення: " + a.divide(b));
    }
}