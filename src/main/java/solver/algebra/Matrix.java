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




}
