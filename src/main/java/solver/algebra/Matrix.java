package solver.algebra;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
    private final int rows;
    private final int cols;
    private final double[][] data;

    // Конструктор за кількістю рядків та стовпців
    public Matrix(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive: " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // Конструктор за двовимірним масивом із валідацією прямокутності та захисним копіюванням
    public Matrix(double[][] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null || arr[0].length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }
        this.rows = arr.length;
        this.cols = arr[0].length;
        this.data = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (arr[i] == null || arr[i].length != cols) {
                throw new IllegalArgumentException("All matrix rows must have the same length (non-jagged array)");
            }
            for (int j = 0; j < cols; j++) {
                if (Double.isNaN(arr[i][j]) || Double.isInfinite(arr[i][j])) {
                    throw new IllegalArgumentException("Matrix elements must be finite numbers");
                }
                this.data[i][j] = arr[i][j];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double get(int i, int j) {
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: [" + i + "][" + j + "]");
        }
        return data[i][j];
    }

    public double[][] getData() {
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(this.data[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    public Matrix multiplyByScalar(double scalar) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return res;
    }

    public Matrix multiplyMatrix(Matrix other) {
        if (other == null) {
            throw new IllegalArgumentException("Operand matrix cannot be null");
        }
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Matrix multiplication dimension mismatch: "
                    + this.cols + " columns != " + other.rows + " rows");
        }

        Matrix res = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0.0;
                for (int k = 0; k < this.cols; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                res.data[i][j] = sum;
            }
        }
        return res;
    }

    public double determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("Determinant is defined only for square matrices: " + rows + "x" + cols);
        }

        int n = rows;
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data[i], 0, a[i], 0, n);
        }

        double det = 1.0;
        for (int i = 0; i < n; i++) {
            int pivot = i;
            while (pivot < n && Math.abs(a[pivot][i]) < 1e-9) {
                pivot++;
            }

            if (pivot == n) {
                return 0.0;
            }

            if (pivot != i) {
                double[] temp = a[i];
                a[i] = a[pivot];
                a[pivot] = temp;
                det = -det;
            }

            for (int k = i + 1; k < n; k++) {
                double factor = a[k][i] / a[i][i];
                for (int j = i; j < n; j++) {
                    a[k][j] -= factor * a[i][j];
                }
            }
            det *= a[i][i];
        }
        return det;
    }

    public double norm2() {
        double sumOfSquares = 0.0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sumOfSquares += this.data[i][j] * this.data[i][j];
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    public double norm(double p) {
        if (Double.isNaN(p) || p < 1.0) {
            throw new IllegalArgumentException("p-norm requires p >= 1.0, but got: " + p);
        }

        if (Double.isInfinite(p)) {
            double max = 0.0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    max = Math.max(max, Math.abs(this.data[i][j]));
                }
            }
            return max;
        }

        double sum = 0.0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += Math.pow(Math.abs(this.data[i][j]), p);
            }
        }
        return Math.pow(sum, 1.0 / p);
    }

    public double trace(int offset) {
        if (offset >= cols || -offset >= rows) {
            return 0.0;
        }
        double sum = 0.0;
        int startRow = Math.max(0, -offset);
        int startCol = Math.max(0, offset);
        int length = Math.min(rows - startRow, cols - startCol);

        for (int idx = 0; idx < length; idx++) {
            sum += this.data[startRow + idx][startCol + idx];
        }
        return sum;
    }

    public double trace() {
        return trace(0);
    }

    public double tracePower(int power) {
        if (rows != cols) {
            throw new IllegalArgumentException("Power trace requires square matrix: " + rows + "x" + cols);
        }
        if (power < 0) {
            throw new IllegalArgumentException("Negative powers are not supported: " + power);
        }
        if (power == 0) {
            return rows;
        }
        if (power == 1) {
            return trace();
        }
        if (power == 2) {
            double sum = 0.0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    sum += this.data[i][j] * this.data[j][i];
                }
            }
            return sum;
        }

        Matrix curr = this;
        for (int p = 1; p < power; p++) {
            curr = curr.multiplyMatrix(this);
        }
        return curr.trace();
    }

    public Matrix inverse() {
        if (rows != cols) {
            throw new IllegalArgumentException("Inverse matrix exists only for square matrices: " + rows + "x" + cols);
        }

        int n = rows;
        double[][] augmented = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data[i], 0, augmented[i], 0, n);
            augmented[i][n + i] = 1.0;
        }

        final double EPS = 1e-12;

        for (int col = 0; col < n; col++) {
            int pivotRow = col;
            double maxVal = Math.abs(augmented[col][col]);
            for (int row = col + 1; row < n; row++) {
                double curVal = Math.abs(augmented[row][col]);
                if (curVal > maxVal) {
                    maxVal = curVal;
                    pivotRow = row;
                }
            }

            if (maxVal < EPS) {
                throw new ArithmeticException("Matrix is singular (determinant is zero), inverse cannot be computed.");
            }

            if (pivotRow != col) {
                double[] temp = augmented[col];
                augmented[col] = augmented[pivotRow];
                augmented[pivotRow] = temp;
            }

            double pivot = augmented[col][col];
            for (int j = col; j < 2 * n; j++) {
                augmented[col][j] /= pivot;
            }

            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = augmented[row][col];
                    if (Math.abs(factor) > EPS) {
                        for (int j = col; j < 2 * n; j++) {
                            augmented[row][j] -= factor * augmented[col][j];
                        }
                    }
                }
            }
        }

        double[][] invData = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmented[i], n, invData[i], 0, n);
        }

        return new Matrix(invData);
    }

    public double[] solve(double[] b) {
        if (rows != cols) {
            throw new IllegalArgumentException("System must have a square coefficient matrix: " + rows + "x" + cols);
        }
        if (b == null || b.length != rows) {
            throw new IllegalArgumentException("Vector b dimension does not match matrix rows");
        }

        int n = rows;
        double[][] a = new double[n][n];
        double[] rhs = new double[n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data[i], 0, a[i], 0, n);
            rhs[i] = b[i];
        }

        final double EPS = 1e-12;

        for (int col = 0; col < n; col++) {
            int pivotRow = col;
            double maxVal = Math.abs(a[col][col]);
            for (int row = col + 1; row < n; row++) {
                double currVal = Math.abs(a[row][col]);
                if (currVal > maxVal) {
                    maxVal = currVal;
                    pivotRow = row;
                }
            }

            if (maxVal < EPS) {
                throw new ArithmeticException("Matrix is singular or system has no unique solution.");
            }

            if (pivotRow != col) {
                double[] tempRow = a[col];
                a[col] = a[pivotRow];
                a[pivotRow] = tempRow;

                double tempB = rhs[col];
                rhs[col] = rhs[pivotRow];
                rhs[pivotRow] = tempB;
            }

            for (int row = col + 1; row < n; row++) {
                double factor = a[row][col] / a[col][col];
                for (int k = col + 1; k < n; k++) {
                    a[row][k] -= factor * a[col][k];
                }
                rhs[row] -= factor * rhs[col];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = rhs[i];
            for (int j = i + 1; j < n; j++) {
                sum -= a[i][j] * x[j];
            }
            x[i] = sum / a[i][i];
        }

        return x;
    }

    public double[] realEigenvals() {
        if (rows != cols) {
            throw new IllegalArgumentException("Eigenvalues are defined only for square matrices: " + rows + "x" + cols);
        }

        int n = rows;
        double[][] h = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data[i], 0, h[i], 0, n);
        }

        for (int col = 0; col < n - 2; col++) {
            int pivotRow = col + 1;
            double maxVal = Math.abs(h[pivotRow][col]);
            for (int row = col + 2; row < n; row++) {
                double curVal = Math.abs(h[row][col]);
                if (curVal > maxVal) {
                    maxVal = curVal;
                    pivotRow = row;
                }
            }

            if (maxVal > 1e-12) {
                if (pivotRow != col + 1) {
                    double[] temp = h[col + 1];
                    h[col + 1] = h[pivotRow];
                    h[pivotRow] = temp;

                    for (int r = 0; r < n; r++) {
                        double t = h[r][col + 1];
                        h[r][col + 1] = h[r][pivotRow];
                        h[r][pivotRow] = t;
                    }
                }

                for (int row = col + 2; row < n; row++) {
                    double factor = h[row][col] / h[col + 1][col];
                    for (int c = col; c < n; c++) {
                        h[row][c] -= factor * h[col + 1][c];
                    }
                    for (int r = 0; r < n; r++) {
                        h[r][col + 1] += factor * h[r][row];
                    }
                }
            }
        }

        double[] buffer = new double[n];
        int count = 0;
        int m = n - 1;
        final double EPS = 1e-10;
        int iterations = 0;
        final int MAX_ITERS = 100 * n;

        while (m >= 0) {
            if (iterations++ > MAX_ITERS) {
                break;
            }

            if (m == 0) {
                buffer[count++] = h[0][0];
                break;
            }

            double pSub = Math.abs(h[m][m - 1]);
            if (pSub <= EPS * (Math.abs(h[m - 1][m - 1]) + Math.abs(h[m][m]))) {
                buffer[count++] = h[m][m];
                m--;
                iterations = 0;
                continue;
            }

            if (m == 1 || Math.abs(h[m - 1][m - 2]) <= EPS * (Math.abs(h[m - 2][m - 2]) + Math.abs(h[m - 1][m - 1]))) {
                double a = h[m - 1][m - 1];
                double b = h[m - 1][m];
                double c = h[m][m - 1];
                double d = h[m][m];

                double tr = a + d;
                double det = a * d - b * c;
                double discr = tr * tr - 4 * det;

                if (discr >= 0) {
                    double sqrtD = Math.sqrt(discr);
                    buffer[count++] = (tr + sqrtD) / 2.0;
                    buffer[count++] = (tr - sqrtD) / 2.0;
                }

                m -= 2;
                iterations = 0;
                continue;
            }

            double a = h[m - 1][m - 1];
            double b = h[m - 1][m];
            double c = h[m][m - 1];
            double d = h[m][m];

            double tr = a + d;
            double det = a * d - b * c;
            double discr = tr * tr - 4 * det;

            double mu = d;
            if (discr >= 0) {
                double l1 = (tr + Math.sqrt(discr)) / 2.0;
                double l2 = (tr - Math.sqrt(discr)) / 2.0;
                mu = (Math.abs(l1 - d) < Math.abs(l2 - d)) ? l1 : l2;
            } else {
                mu = tr / 2.0;
            }

            for (int i = 0; i <= m; i++) {
                h[i][i] -= mu;
            }

            double[] cos = new double[m];
            double[] sin = new double[m];

            for (int i = 0; i < m; i++) {
                double xi = h[i][i];
                double yi = h[i + 1][i];
                double r = Math.hypot(xi, yi);

                if (r < 1e-14) {
                    cos[i] = 1.0;
                    sin[i] = 0.0;
                } else {
                    cos[i] = xi / r;
                    sin[i] = -yi / r;
                }

                for (int j = i; j <= m; j++) {
                    double t1 = h[i][j];
                    double t2 = h[i + 1][j];
                    h[i][j] = cos[i] * t1 - sin[i] * t2;
                    h[i + 1][j] = sin[i] * t1 + cos[i] * t2;
                }
            }

            for (int i = 0; i < m; i++) {
                for (int r = 0; r <= Math.min(i + 2, m); r++) {
                    double t1 = h[r][i];
                    double t2 = h[r][i + 1];
                    h[r][i] = cos[i] * t1 - sin[i] * t2;
                    h[r][i + 1] = sin[i] * t1 + cos[i] * t2;
                }
            }

            for (int i = 0; i <= m; i++) {
                h[i][i] += mu;
            }
        }

        return Arrays.copyOf(buffer, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return rows == matrix.rows && cols == matrix.cols && Arrays.deepEquals(data, matrix.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix ").append(rows).append("x").append(cols).append(":\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }
        return sb.toString();
    }
}
