package solver.algebra;

import java.util.Arrays;
import java.util.Objects;

public class NumericArray {

    private final double[] data;
    private static final double EPSILON = 1e-9;

    private static void validateValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Array elements must be finite numbers");
        }
    }

    // При null створюється порожній масив (узгоджено з NumericArrayTest)
    public NumericArray(double[] data) {
        if (data == null) {
            this.data = new double[0];
        } else {
            for (double val : data) {
                validateValue(val);
            }
            this.data = Arrays.copyOf(data, data.length);
        }
    }

    public int size() {
        return data.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + data.length);
        }
    }

    public double get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, double value) {
        checkIndex(index);
        validateValue(value);
        data[index] = value;
    }

    public double sum() {
        double sum = 0.0;
        for (double val : data) {
            sum += val;
        }
        return sum;
    }

    public double mean() {
        if (data.length == 0) {
            throw new IllegalStateException("Array is empty");
        }
        double m = 0.0;
        for (int i = 0; i < data.length; i++) {
            m += (data[i] - m) / (i + 1);
        }
        return m;
    }

    public double min() {
        if (data.length == 0) {
            throw new IllegalStateException("Cannot evaluate minimum on an empty array");
        }
        double min = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    public double max() {
        if (data.length == 0) {
            throw new IllegalStateException("Cannot evaluate maximum on an empty array");
        }
        double max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    // Делегування на BasicStatic (Task A2)
    public double variance() {
        return BasicStatic.variance(this.data);
    }

    public double sampleVariance() {
        return BasicStatic.sampleVariance(this.data);
    }

    public double standardDeviation() {
        return BasicStatic.standardDeviation(this.data);
    }

    public double sampleStandardDeviation() {
        return BasicStatic.sampleStandardDeviation(this.data);
    }

    // Task A4: нормалізація Min-Max
    public NumericArray normalize() {
        if (data.length == 0) {
            return new NumericArray(new double[0]);
        }

        double minVal = min();
        double maxVal = max();
        double range = maxVal - minVal;
        double[] normalized = new double[data.length];

        if (range == 0.0) {
            Arrays.fill(normalized, 0.0);
            return new NumericArray(normalized);
        }

        if (Double.isInfinite(range)) {
            double halfMin = minVal / 2.0;
            double halfMax = maxVal / 2.0;
            double halfRange = halfMax - halfMin;

            for (int i = 0; i < data.length; i++) {
                normalized[i] = ((data[i] / 2.0) - halfMin) / halfRange;
            }
            return new NumericArray(normalized);
        }

        for (int i = 0; i < data.length; i++) {
            normalized[i] = (data[i] - minVal) / range;
        }

        return new NumericArray(normalized);
    }

    public double[] toArray() {
        return Arrays.copyOf(data, data.length);
    }

    // Task A3: пошук і підрахунок
    private boolean floatEquals(double a, double b) {
        if (Double.compare(a, b) == 0) {
            return true;
        }
        return Math.abs(a - b) <= EPSILON;
    }

    public int indexOf(double value) {
        for (int i = 0; i < data.length; i++) {
            if (floatEquals(data[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(double value) {
        return indexOf(value) != -1;
    }

    public int count(double value) {
        int count = 0;
        for (double v : data) {
            if (floatEquals(v, value)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericArray that = (NumericArray) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
