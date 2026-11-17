package solver.algebra;

import java.math.BigInteger;
import java.util.Objects;

public class Fraction implements Comparable<Fraction> {
    private final long numerator;
    private final long denominator;

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        if (denominator == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Denominator cannot be Long.MIN_VALUE");
        }
        if (denominator < 0) {
            if (numerator == Long.MIN_VALUE) {
                throw new IllegalArgumentException("Numerator overflow when normalizing sign");
            }
            numerator = -numerator;
            denominator = -denominator;
        }

        if (numerator == 0) {
            this.numerator = 0;
            this.denominator = 1;
            return;
        }

        long divisor = gcd(numerator, denominator);
        this.numerator = numerator / divisor;
        this.denominator = denominator / divisor;
    }

    private static long gcd(long a, long b) {
        // Працюємо у від'ємній області, щоб уникнути переповнення при a == Long.MIN_VALUE
        a = a > 0 ? -a : a;
        b = b > 0 ? -b : b;
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return -a;
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public Fraction add(Fraction other) {
        Objects.requireNonNull(other, "Operand fraction cannot be null");
        long newNumerator = Math.addExact(
                Math.multiplyExact(this.numerator, other.denominator),
                Math.multiplyExact(other.numerator, this.denominator)
        );
        long newDenominator = Math.multiplyExact(this.denominator, other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        Objects.requireNonNull(other, "Operand fraction cannot be null");
        long newNumerator = Math.subtractExact(
                Math.multiplyExact(this.numerator, other.denominator),
                Math.multiplyExact(other.numerator, this.denominator)
        );
        long newDenominator = Math.multiplyExact(this.denominator, other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction multiply(Fraction other) {
        Objects.requireNonNull(other, "Operand fraction cannot be null");
        long newNumerator = Math.multiplyExact(this.numerator, other.numerator);
        long newDenominator = Math.multiplyExact(this.denominator, other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction divide(Fraction other) {
        Objects.requireNonNull(other, "Divisor fraction cannot be null");
        if (other.numerator == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        long newNumerator = Math.multiplyExact(this.numerator, other.denominator);
        long newDenominator = Math.multiplyExact(this.denominator, other.numerator);
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
        return numerator == other.numerator && denominator == other.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public int compareTo(Fraction other) {
        Objects.requireNonNull(other, "Compared fraction cannot be null");
        BigInteger left = BigInteger.valueOf(numerator)
                .multiply(BigInteger.valueOf(other.denominator));
        BigInteger right = BigInteger.valueOf(other.numerator)
                .multiply(BigInteger.valueOf(denominator));
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