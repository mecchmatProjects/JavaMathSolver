package solver.algebra;

import java.util.Objects;

public class ComplexNumber {
    private final double real;
    private final double imaginary;

    public ComplexNumber(double real, double imaginary) {
        if (Double.isNaN(real) || Double.isInfinite(real)
                || Double.isNaN(imaginary) || Double.isInfinite(imaginary)) {
            throw new IllegalArgumentException("Complex number components must be finite numbers");
        }
        this.real = real + 0.0;
        this.imaginary = imaginary + 0.0;
    }

    private static ComplexNumber ofArithmeticResult(double real, double imaginary) {
        if (Double.isNaN(real) || Double.isInfinite(real)
                || Double.isNaN(imaginary) || Double.isInfinite(imaginary)) {
            throw new ArithmeticException("Complex number arithmetic overflow");
        }
        return new ComplexNumber(real, imaginary);
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public ComplexNumber add(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand complex number cannot be null");
        }
        return ofArithmeticResult(this.real + other.real, this.imaginary + other.imaginary);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand complex number cannot be null");
        }
        return ofArithmeticResult(this.real - other.real, this.imaginary - other.imaginary);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand complex number cannot be null");
        }
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImaginary = this.real * other.imaginary + this.imaginary * other.real;
        return ofArithmeticResult(newReal, newImaginary);
    }

    public double abs() {
        return Math.hypot(real, imaginary);
    }

    public ComplexNumber divide(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Divisor complex number cannot be null");
        }
        double c = other.real;
        double d = other.imaginary;
        if (c == 0.0 && d == 0.0) {
            throw new ArithmeticException("Division by zero (divisor is zero complex number)");
        }
        double newReal;
        double newImaginary;
        if (Math.abs(c) >= Math.abs(d)) {
            double r = d / c;
            double denom = c + d * r;
            newReal = (this.real + this.imaginary * r) / denom;
            newImaginary = (this.imaginary - this.real * r) / denom;
        } else {
            double r = c / d;
            double denom = c * r + d;
            newReal = (this.real * r + this.imaginary) / denom;
            newImaginary = (this.imaginary * r - this.real) / denom;
        }
        return ofArithmeticResult(newReal, newImaginary);
    }

    @Override
    public String toString() {
        if (imaginary == 0.0) {
            return String.valueOf(real);
        }
        if (real == 0.0) {
            return imaginary + "i";
        }
        return imaginary > 0
                ? real + " + " + imaginary + "i"
                : real + " - " + Math.abs(imaginary) + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexNumber that = (ComplexNumber) o;
        return Double.compare(that.real, real) == 0
                && Double.compare(that.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }
}