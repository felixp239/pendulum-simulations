package com.physics;

public class Matrix {
    private float[][] values;

    public Matrix(float[][] values) throws Exception { //can be a problem if values[i].length != values[j].length
        this.values = values;
        for (int i = 0; i < values.length - 1; i++)
            if (values[i].length != values[i + 1].length)
                throw new Exception("Non matrix 2D array given");
    }

    public static float[][] sum(Matrix m1, Matrix m2) throws Exception {
        if (m1.values.length != m2.values.length || m1.values[0].length != m2.values[0].length)
            throw new Exception("Matrices are not addable");
        float[][] result = new float[m1.values.length][m1.values[0].length];
        for (int i = 0; i < m1.values.length; i++) {
            for (int j = 0; j < m1.values[0].length; i++) {
                result[i][j] = m1.values[i][j] + m2.values[i][j];
            }
        }
        return result;
    }

    public void add(Matrix matrix) throws Exception {
        values = sum(this, matrix);
    }

    public static float[][] product(Matrix matrix, float f) {
        float[][] result = new float[matrix.values.length][matrix.values[0].length];
        for (int i = 0; i < matrix.values.length; i++) {
            for (int j = 0; j < matrix.values[0].length; j++) {
                result[i][j] = matrix.values[i][j] * f;
            }
        }
        return result;
    }

    public static float[][] product(Matrix m1, Matrix m2) throws Exception {
        if (m1.values[0].length != m2.values.length)
            throw new Exception("Product of matrices undefined");

        float[][] result = new float[m1.values.length][m2.values[0].length];
        for (int i = 0; i < m1.values.length; i++) {
            for (int j = 0; j < m2.values[0].length; j++) {
                for (int k = 0; k < m1.values[0].length; k++) {
                    result[i][j] = m1.values[i][k] * m2.values[k][j];
                }
            }
        }
        return result;
    }

    public void multiply(float f) {
        this.values = product(this, f);
    }

    public void multiply(Matrix matrix) throws Exception {
        this.values = product(this, matrix);
    }

    public static float determinant(Matrix matrix) throws Exception {
        if (matrix.values.length != matrix.values[0].length)
            throw new Exception("Trying to find determinant of a non square matrix");
        if (matrix.values.length == 1)
            return 0;
        if (matrix.values.length == 2)
            return matrix.values[0][0] * matrix.values[1][1] - matrix.values[0][1] * matrix.values[1][0];
        float result = 0;
        for (int i = 0, sign = 1; i < matrix.values.length; i++) {
            result += sign * matrix.values[0][i] * determinant(matrix.reduceMatrix(0, i));
            sign *= -1;
        }
        return result;
    }

    private Matrix reduceMatrix(int row, int column) throws Exception {
        float[][] result = new float[values.length - 1][values[0].length - 1];
        for (int ir = 0, im = 0; ir < values.length - 1; ir++, im++) {
            if (im == row)
                im++;
            for (int jr = 0, jm = 0; jr < values[0].length - 1; jr++, jm++) {
                if (jm == column)
                    jm++;
                result[ir][jr] = values[im][jm];
            }
        }
        return new Matrix(result);
    }

    @Override
    public String toString() {
        String result = "-\n";
        for (int i = 0; i < values.length; i++) {
            result += "{";
            for (int j = 0; j < values[0].length; j++) {
                result += values[i][j];
                if (j != values[0].length - 1)
                    result += ", ";
            }
            result += "}\n";
        }
        result += "-";
        return result;
    }
}
