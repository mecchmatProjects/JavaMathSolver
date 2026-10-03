package solver.algebra;

/**
 * ALG Lab 1: базова арифметика.
 */
public final class Arithmetic {

    private Arithmetic() {
    }

    // ALGEBRA (ALG-01 ... ALG-06)
    public static double add(double a, double b) { return a + b; }

    public static double sub(double a, double b) { return a - b; }

    public static double mul(double a, double b) { return a * b; }

    public static double div(double a, double b) { return a / b; }

    public static double pow(double a, double b) { return Math.pow(a, b); }

    public static double abs(double a) { return Math.abs(a); }

    public static double sqrt(double a) { return Math.sqrt(a); }
}
