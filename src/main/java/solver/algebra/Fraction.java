package solver.algebra;

public class Fraction implements Comparable<Fraction> {
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

    public Fraction multiply(Fraction other) {
        long newNumerator = numerator * other.numerator;
        long newDenominator = denominator * other.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException(
                    "Ділити на нуль не можна"
            );
        }

        long newNumerator = numerator * other.denominator;
        long newDenominator = denominator * other.numerator;

        return new Fraction(newNumerator, newDenominator);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Fraction)) {
            return false;
        }

        Fraction other = (Fraction) obj;

        return numerator == other.numerator
                && denominator == other.denominator;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(numerator, denominator);
    }

    @Override
    public int compareTo(Fraction other) {
        java.math.BigInteger left =
                java.math.BigInteger.valueOf(numerator)
                        .multiply(java.math.BigInteger.valueOf(other.denominator));

        java.math.BigInteger right =
                java.math.BigInteger.valueOf(other.numerator)
                        .multiply(java.math.BigInteger.valueOf(denominator));

        return left.compareTo(right);
    }



    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }

        return numerator + "/" + denominator;
    }
    
}
