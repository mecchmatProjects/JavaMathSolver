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





}
