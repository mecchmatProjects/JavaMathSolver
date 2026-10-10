public class NumericArray {
    private final double[] array;
    
    private void validate(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Array elements must be finite numbers");
        }
    }
    
    public NumericArray(double[] array) {
        if (array == null) {
            this.array = new double[0];
        } else {
            for (double val : array) {
                validate(val); 
            }
            this.array = array.clone();
        }
    }
    
    public int size() {
        return array.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + array.length);
        }
    }


    
    public double get(int index) {
        checkIndex(index);
        return array[index];
    }

    
    public void set(int index, double value) {
        checkIndex(index);
        validate(value);
        array[index] = value;
    }

    public double sum() {
        double sum = 0;
        for (double val : array) {
            sum += val;
        }
        return sum;
    }

    public double mean() {
        if (array.length == 0) {
            throw new IllegalStateException("Array is empty");
        }

        double mean = 0.0;
        for (int i = 0; i < array.length; i++) {
            mean += (array[i] - mean) / (i + 1);
        }
        return mean;
    }

    public double min() {
        if (array.length == 0) {
            throw new IllegalStateException("Array is empty");
        }
        double minVal = array[0];
        for (double val : array) {
            if (val < minVal) {
                minVal = val;
            }
        }
        return minVal;
    }

    public double max() {
        if (array.length == 0) {
            throw new IllegalStateException("Array is empty");
        }
        double maxVal = array[0];
        for (double val : array) {
            if (val > maxVal) {
                maxVal = val;
            }
        }
        return maxVal;
    }
}
