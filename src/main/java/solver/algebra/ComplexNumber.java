package solver.algebra;

import java.util.Objects;

public class ComplexNumber {
    private final double real;
    private final double imaginary;

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
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
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand complex number cannot be null");
        }
        return new ComplexNumber(this.real - other.real, this.imaginary - other.imaginary);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand complex number cannot be null");
        }
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImaginary = this.real * other.imaginary + this.imaginary * other.real;
        return new ComplexNumber(newReal, newImaginary);
    }

    public double abs() {
        return Math.hypot(real, imaginary);
    }

    public ComplexNumber divide(ComplexNumber other) {
        if (other == null) {
            throw new IllegalArgumentException("Divisor complex number cannot be null");
        }
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        if (denominator == 0.0) {
            throw new ArithmeticException("Division by zero (divisor is zero complex number)");
        }
        double newReal = (this.real * other.real + this.imaginary * other.imaginary) / denominator;
        double newImaginary = (this.imaginary * other.real - this.real * other.imaginary) / denominator;
        return new ComplexNumber(newReal, newImaginary);
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