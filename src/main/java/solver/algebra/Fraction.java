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
        BigInteger n = BigInteger.valueOf(numerator);
        BigInteger d = BigInteger.valueOf(denominator);
        BigInteger divisor = n.gcd(d);
        n = n.divide(divisor);
        d = d.divide(divisor);
        if (d.signum() < 0) {
            n = n.negate();
            d = d.negate();
        }
        this.numerator = n.longValueExact();
        this.denominator = d.longValueExact();
    }

    private static long gcd(long a, long b) {
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
        long commonGcd = gcd(this.denominator, other.denominator);
        long leftScale = other.denominator / commonGcd;
        long rightScale = this.denominator / commonGcd;

        long newNumerator = Math.addExact(
                Math.multiplyExact(this.numerator, leftScale),
                Math.multiplyExact(other.numerator, rightScale)
        );
        long newDenominator = Math.multiplyExact(this.denominator, leftScale);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        Objects.requireNonNull(other, "Operand fraction cannot be null");
        long commonGcd = gcd(this.denominator, other.denominator);
        long leftScale = other.denominator / commonGcd;
        long rightScale = this.denominator / commonGcd;

        long newNumerator = Math.subtractExact(
                Math.multiplyExact(this.numerator, leftScale),
                Math.multiplyExact(other.numerator, rightScale)
        );
        long newDenominator = Math.multiplyExact(this.denominator, leftScale);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction multiply(Fraction other) {
        Objects.requireNonNull(other, "Operand fraction cannot be null");
        long g1 = gcd(this.numerator, other.denominator);
        long g2 = gcd(other.numerator, this.denominator);

        long newNumerator = Math.multiplyExact(this.numerator / g1, other.numerator / g2);
        long newDenominator = Math.multiplyExact(this.denominator / g2, other.denominator / g1);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction divide(Fraction other) {
        Objects.requireNonNull(other, "Divisor fraction cannot be null");
        if (other.numerator == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        long g1 = gcd(this.numerator, other.numerator);
        long g2 = gcd(other.denominator, this.denominator);

        long numPart = other.numerator / g1;
        long denPart = other.denominator / g2;
        if (numPart < 0) {
            if (numPart == Long.MIN_VALUE || denPart == Long.MIN_VALUE) {
                throw new ArithmeticException("Overflow when normalizing sign in division");
            }
            numPart = -numPart;
            denPart = -denPart;
        }

        long newNumerator = Math.multiplyExact(this.numerator / g1, denPart);
        long newDenominator = Math.multiplyExact(this.denominator / g2, numPart);
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