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

    public double[] solve(double[] b){
        if (rows != cols){
            System.out.println("Error: System must have a square coefficient matrix. Current size: " + rows + "x" + cols);
            return null;
        }
        if (b == null || b.length != rows){
            System.out.println("Error: Vector b dimension (" + (b == null ? 0 : b.length) + ") does not match matrix rows (" + rows + ").");
            return null;
        }

        int n = rows;

        double[][] a = new double[n][n];
        double[] rhs = new double[n];

        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                a[i][j] = this.data[i][j];
            }
            rhs[i] = b[i];
        }

        final double EPS = 1e-12;

        for (int col = 0; col < n; col++){
            int pivotRow = col;
            double maxVal = Math.abs(a[col][col]);
            for (int row = col + 1; row < n; row++){
                double currVal = Math.abs(a[row][col]);
                if (currVal > maxVal){
                    maxVal = currVal;
                    pivotRow = row;
                }
            }

            if (maxVal < EPS){
                System.out.println("Error: Matrix is singular or system has no unique solution.");
                return null;
            }

            if (pivotRow != col){
                double[] tempRow = a[col];
                a[col] = a[pivotRow];
                a[pivotRow] = tempRow;

                double tempB = rhs[col];
                rhs[col] = rhs[pivotRow];
                rhs[pivotRow] = tempB;
            }

            for (int row = col + 1; row < n; row++){
                double factor = a[row][col] / a[col][col];
                for (int k = col + 1; k < n; k++){
                    a[row][k] -= factor * a[col][k];
                }
                rhs[row] -= factor * rhs[col];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--){
            double sum = rhs[i];
            for (int j = i + 1; j < n; j++){
                sum -= a[i][j] * x[j];
            }
            x[i] = sum / a[i][i];
        }

        return x;

    }

    public double[] realEigenvals(){
        if (rows != cols) {
            System.out.println("Error: Eigenvalues are defined only for square matrices. Current size: " + rows + "x" + cols);
            return null;
        }

        int n = rows;

        double[][] h = new double[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                h[i][j] = this.data[i][j];
            }
        }

        /// далі описано підхід зведення до форми хессенберга з наступним
        ///QR-розкладом зі зсувом Вілкінсона та дефляцією.
        ///Очікувана складність алгоритму O(n^3)


        for (int col = 0; col < n -2; col++){
            int pivotRow = col + 1;
            double maxVal = Math.abs(h[pivotRow][col]);
            for (int row = col + 2; row < n; row++) {
                double curVal = Math.abs(h[row][col]);
                if (curVal > maxVal) {
                    maxVal = curVal;
                    pivotRow = row;
                }
            }

            if (maxVal > 1e-12){
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
        boolean hasComplex = false;
        int m = n - 1;
        final double EPS = 1e-10;
        int iterations = 0;
        final int MAX_ITERS = 100 * n;

        while (m >= 0) {
            if (iterations++ > MAX_ITERS) {
                System.out.println("Warning: Max iterations reached. Convergence stopped.");
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
                } else {
                    hasComplex = true;
                }

                m -= 2;
                iterations = 0;
                continue;
            }

            // Wilkinson shift
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

            // H - mu * I
            for (int i = 0; i <= m; i++) {
                h[i][i] -= mu;
            }

            // Givens Rotations
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

        if (hasComplex) {
            System.out.println("Note: Matrix also has complex conjugate eigenvalues that were omitted.");
        }

        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[i];
        }

        return result;
    }






}
