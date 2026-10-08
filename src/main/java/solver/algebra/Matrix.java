package solver.algebra;

public class Matrix {
    public int rows;
    public int cols;
    public double[][] data;

    // конструктор за кількістю рядків та стовпців
    public Matrix(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // конструктор за двовимірним масивом
    public Matrix(double[][] arr){
        this.rows = arr.length;
        this.cols = arr[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
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
        return data[i][j];
    }

    // метод множення матриці на скаляр
    public Matrix multiplyByScalar(double scalar){
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                res.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return res;
    }

    // метод множення матриць
    public Matrix multiplyMatrix(Matrix other){
        if (this.cols != other.rows){
            System.out.println("Error: number of columns of the first matrix should be equal to the number of rows of the second matrix");
            return null;
        }

        Matrix res = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++){
            for (int j = 0; j < other.cols; j++){
                double sum = 0;
                for (int k = 0; k < this.cols; k++){
                    sum += this.data[i][k] * other.data[k][j];
                }
                res.data[i][j] = sum;
            }
        }
        return res;
    }

    public double determinant(){
        if (rows != cols){
            System.out.println("Error: determinant is defined only for square matrices. Current dimensions: " + rows + "x" + cols);
            return Double.NaN;
        }

        int n = rows;

        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = this.data[i][j];
            }
        }
        double det = 1.0;
        for (int i = 0; i < n; i++){
            int pivot = i;
            while (pivot < n && Math.abs(a[pivot][i]) < 1e-9) {
                pivot++;
            }

            if (pivot == n) {
                return 0.0;
            }

            if (pivot != i){
                double[] temp = a[i];
                a[i] = a[pivot];
                a[pivot] = temp;
                det = -det;
            }

            for (int k = i+1; k<n; k++){
                double factor = a[k][i] / a[i][i];
                for (int j = i; j < n; j++){
                    a[k][j] -= factor * a[i][j];
                }
            }
            det *= a[i][i];
        }
        return det;
    }

    public double norm2(){
        double sumOfSquares = 0;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                sumOfSquares += this.data[i][j] * this.data[i][j];
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    public double norm(double p){
        if (p < 1.0){
            System.out.println("Error: p-norm requires p >= 1");
            return Double.NaN;
        }

        if (Double.isInfinite(p)){
            double max = 0.0;
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++){
                    max = Math.max(max, Math.abs(this.data[i][j]));
                }
            }
            return max;
        }

        double sum = 0.0;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                sum += Math.pow(Math.abs(this.data[i][j]), p);
            }
        }
        return Math.pow(sum, 1.0 / p);
    }

    public double trace(int offset){
        if (offset >= cols || -offset >= rows){
            System.out.println("Warning: Offset " + offset + " is out of matrix bounds (" + rows + "x" + cols + ").");
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

    public double trace(){
        return trace(0);
    }

    public double tracePower(int power){
        if (rows != cols){
            System.out.println("Error: Power trace requires square matrix.");
            return Double.NaN;
        }
        if (power < 0){
            System.out.println("Error: Negative powers are not supported without explicit inversion.");
            return Double.NaN;
        }
        if (power == 0) {
            return rows; // tr(I_n) = n
        }
        if (power == 1) {
            return trace();
        }
        if (power == 2){
            double sum = 0.0;
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++){
                    sum += this.data[i][j] * this.data[j][i];
                }
            }
            return sum;
        }

        Matrix curr = this;
        for (int p = 1; p < power; p++){
            curr = curr.multiplyMatrix(this);
        }
        return curr.trace();
    }

    // побудова оберненої матриці методом Гаусса-Йордана, очікувана складність O(n^3)
    public Matrix inverse(){
        if (rows != cols){
            System.out.println("Error: Inverse matrix exists only for square matrixes. Current size: " + rows + "x" + cols);
            return null;
        }

        int n = rows;

        //створити розширену матрицю [A | I] розміру n x 2n
        double[][] augmented = new double[n][2*n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++) {
                augmented[i][j] = this.data[i][j];
            }
            augmented[i][n+i] = 1.0;
        }
        final double EPS = 1e-12;

        for (int col = 0; col < n; col++){
            int pivotRow = col;
            double maxVal = Math.abs(augmented[col][col]);
            for (int row = col + 1; row < n; row++) {
                double curVal = Math.abs(augmented[row][col]);
                if (curVal > maxVal) {
                    maxVal = curVal;
                    pivotRow = row;
                }
            }

            if (maxVal < EPS){
                System.out.println("Error: Matrix is singular (determinant is zero), inverse cannot be computed.");
                return null;
            }

            if (pivotRow != col){
                double[] temp = augmented[col];
                augmented[col] = augmented[pivotRow];
                augmented[pivotRow] = temp;
            }

            double pivot = augmented[col][col];
            for (int j = col; j < 2 * n; j++){
                augmented[col][j] /= pivot;
            }

            for (int row = 0; row < n; row++){
                if (row != col){
                    double factor = augmented[row][col];
                    if (Math.abs(factor) > EPS){
                        for (int j = col; j < 2 * n; j++){
                            augmented[row][j] -= factor * augmented[col][j];
                        }
                    }
                }
            }
        }
        double[][] invData = new double[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                invData[i][j] = augmented[i][n+j];
            }
        }

        return new Matrix(invData);

    }





}
