package solver.algebra;

public class Fraction {
    private final long numerator;
    private final long denominator;

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException(
                    "Знаменник не може дорівнювати нулю"
            );
        }

        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        long divisor = gcd(Math.abs(numerator), denominator);

        this.numerator = numerator / divisor;
        this.denominator = denominator / divisor;
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public Fraction add(Fraction other) {
        long newNumerator =
                numerator * other.denominator
                        + other.numerator * denominator;

        long newDenominator = denominator * other.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        long newNumerator =
                numerator * other.denominator
                        - other.numerator * denominator;

        long newDenominator = denominator * other.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }

        return numerator + "/" + denominator;
    }
    
}
