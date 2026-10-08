package solver.algebra;

import java.util.Arrays;
import java.util.Objects;

public class NumericArray {

    private final double[] data;

    public NumericArray(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        for (double value : data) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Array elements must be finite numbers");
            }
        }
        this.data = Arrays.copyOf(data, data.length);
    }

    public int size() {
        return data.length;
    }

    public double get(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + data.length);
        }
        return data[index];
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

    public NumericArray normalize() {
        if (data.length == 0) {
            return new NumericArray(new double[0]);
        }

        double min = data[0];
        double max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
            }
            if (data[i] > max) {
                max = data[i];
            }
        }

        double range = max - min;
        double[] normalized = new double[data.length];

        if (range == 0.0) {
            Arrays.fill(normalized, 0.0);
            return new NumericArray(normalized);
        }

        if (Double.isInfinite(range)) {
            double halfMin = min / 2.0;
            double halfMax = max / 2.0;
            double halfRange = halfMax - halfMin;

            for (int i = 0; i < data.length; i++) {
                normalized[i] = ((data[i] / 2.0) - halfMin) / halfRange;
            }
            return new NumericArray(normalized);
        }

        for (int i = 0; i < data.length; i++) {
            normalized[i] = (data[i] - min) / range;
        }

        return new NumericArray(normalized);
    }

    public double[] toArray() {
        return Arrays.copyOf(data, data.length);
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
  
    private static final double EPSILON = 1e-9;

    public int indexOf(double value) {
        if (data == null) return -1;
      
        for (int i = 0; i < data.length; i++) {
            if (Math.abs(data[i] - value) <= EPSILON) {
                return i;
            }
        }
        return -1;
    }
  
}
