package com.physics;

import java.awt.*;

public class MultiplePendulum {
    private float[] length;
    private float[] mass;
    private float[][] parameters = new float[2][]; //parameters[0] - theta, parameters[1] - omega
    private float a_x;
    private float a_y;
    private float g;
    private int n;

    private long time;
    private long prev_time;

    public MultiplePendulum(float[] mass, float[] length, float[] theta, float[] omega, float g) throws Exception {
        this.mass = mass;
        this.length = length;
        this.parameters[0] = theta;
        this.parameters[1] = omega;
        this.g = g;
        this.n = mass.length;
        this.a_x = 0;
        this.a_y = 0;
        if (mass.length != length.length || length.length != theta.length || theta.length != omega.length)
            throw new Exception("Not enough data for a model");
        if (n < 2)
            throw new Exception("It's supposed to be MULTIPLE pendulums");
    }

    public MultiplePendulum(float[] length, float[] theta, float[] omega, float g) throws Exception {
        this(new float[length.length], length, theta, omega, g);
        for (int i = 0; i < length.length; i++)
            mass[i] = 1f;
    }

    public MultiplePendulum(float[] length, float[] theta, float g) throws Exception {
        this(length, theta, new float[length.length], g);
        for (int i = 0; i < length.length; i++)
            parameters[1][i] = 0;
    }

    public MultiplePendulum(float[] theta, float g) throws Exception {
        this(new float[theta.length], theta, g);
        for (int i = 0; i < length.length; i++)
            length[i] = 1f;
    }

    public MultiplePendulum(int n) throws Exception {
        this(new float[n], 9.81f);
        for (int i = 0, k = 1; i < n; i++, k *= 2) {
            length[i] = (float) (1f / Math.sqrt(k));
            parameters[0][i] = (float) Math.toRadians(90 - i * 10);
        }
    }

    public void initMultiplePendulum() {
        this.prev_time = System.nanoTime();
    }

    public void update(float a_x, float a_y) {
        time = System.nanoTime();
        float delta = (time - prev_time) / 1000000000f;
        //float delta = 0.01f;

        this.a_x = a_x;
        this.a_y = a_y;
        parameters = calculateParameters(parameters, delta);

        prev_time = time;
    }

    private float[][] calculateParameters(float[][] x_n, float delta) { //x_n - theta1, theta2, omega1, omega2
        float[][] a_n = new float[2][n]; //[0][i] - omega[i], [1][i] - beta[i]
        float[][] b_n = new float[2][n];
        float[][] c_n = new float[2][n];
        float[][] d_n = new float[2][n];

        getDerivatives(a_n, x_n);
        getDerivatives(b_n, getValues(x_n, a_n, delta / 2));
        getDerivatives(c_n, getValues(x_n, b_n, delta / 2));
        getDerivatives(d_n, getValues(x_n, c_n, delta));

        float[][] resultDerivative = new float[2][n];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < n; j++) {
                resultDerivative[i][j] = a_n[i][j] + 2 * b_n[i][j] + 2 * c_n[i][j] + d_n[i][j];
                resultDerivative[i][j] /= 6;
            }
        }

        return getValues(x_n, resultDerivative, delta);
    }

    private void getDerivatives(float[][] derivatives, float[][] values) {
        derivatives[0] = values[1];
        derivatives[1] = calculateBetas(values);
    }

    private float[] calculateBetas(float[][] values) {

        float[][] equations = new float[n][n];
        float[] answers = new float[n];
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                equations[i][i] = 1 / mass[i];
                equations[i][i + 1] = (float) (-Math.cos(values[0][i + 1] - values[0][i]) / mass[i]);
                answers[i] = (float) (length[i] * values[1][i] * values[1][i] + g * Math.cos(values[0][i]) - a_x * Math.sin(values[0][i]) - a_y * Math.cos(values[0][i]));
            }
            else if (i == n - 1) {
                equations[i][i - 1] = (float) (-Math.cos(values[0][i] - values[0][i - 1]) / mass[i - 1]);
                equations[i][i] = 1 / mass[i] + 1 / mass[i - 1];
                answers[i] = length[i] * values[1][i] * values[1][i];
            }
            else {
                equations[i][i - 1] = (float) (-Math.cos(values[0][i] - values[0][i - 1]) / mass[i - 1]);
                equations[i][i] = 1 / mass[i] + 1 / mass[i - 1];
                equations[i][i + 1] = (float) (-Math.cos(values[0][i + 1] - values[0][i]) / mass[i]);
                answers[i] = length[i] * values[1][i] * values[1][i];
            }
        }
        for (int k = 0; k < n - 1; k++) {
            if (equations[k + 1][k] > equations[k][k]) {
                float[] temp = equations[k];
                equations[k] = equations[k + 1];
                equations[k + 1] = temp;
                float t = answers[k];
                answers[k] = answers[k + 1];
                answers[k + 1] = t;
            }

            for (int i = k + 1; i < n; i++) {
                float factor = equations[i][k] / equations[k][k];
                answers[i] -= answers[k] * factor;
                for (int j = k; j < n; j++) {
                    equations[i][j] -= equations[k][j] * factor;
                }
            }
        }
        float[] T = new float[n];
        T[n - 1] = answers[n - 1] / equations[n - 1][n - 1];
        for (int i = n - 2; i >= 0; i--) {
            T[i] = answers[i];
            for (int j = i + 1; j < n; j++) {
                T[i] -= equations[i][j] * T[j];
            }
            T[i] /= equations[i][i];
        }


        //---------------------------------//
        /*
        float[] a = new float[n];
        float[] b = new float[n];
        float[] c = new float[n];
        float[] d = new float[n];
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                a[i] = 0;
                b[i] = 1 / mass[i];
                c[i] = (float) (-Math.cos(values[0][i + 1] - values[0][i]) / mass[i]);
                d[i] = (float) (length[i] * values[1][i] * values[1][i] + g * Math.cos(values[0][i]) - a_x * Math.sin(values[0][i]) - a_y * Math.cos(values[0][i]));
            }
            else if (i == n - 1) {
                 a[i] = (float) (-Math.cos(values[0][i] - values[0][i - 1]) / mass[i - 1]);
                 b[i] = 1 / mass[i] + 1 / mass[i - 1];
                 c[i] = 0;
                 d[i] = length[i] * values[1][i] * values[1][i];
            }
            else {
                a[i] = (float) (-Math.cos(values[0][i] - values[0][i - 1]) / mass[i - 1]);
                b[i] = 1 / mass[i] + 1 / mass[i - 1];
                c[i] = (float) (Math.cos(values[0][i + 1] - values[0][i]) / mass[i]);
                d[i] = length[i] * values[1][i] * values[1][i];
            }
        }
        float[] temp = new float[4];
        for (int i = 1; i < n - 1; i++) {
            temp[0] = 0;
            temp[1] = b[i - 1] * b[i] - a[i] * c[i - 1];
            temp[2] = b[i - 1] * c[i];
            temp[3] = b[i - 1] * d[i] - a[i] * d[i - 1];
            a[i] = temp[0];
            b[i] = temp[1];
            c[i] = temp[2];
            d[i] = temp[3];
        }
        float[] T = new float[n];
        T[n - 1] = (b[n - 2] * d[n - 1] - a[n - 1] * d[n - 2]) / (b[n - 1] * b[n - 1] - c[n - 2] * a[n - 1]);
        for (int i = n - 2; i >= 0; i--) {
            T[i] = (d[i] - c[i] * T[i + 1]) / b[i];
        }
        */
        //-----------------------------------------//

        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                result[i] = (float) (T[i + 1] * Math.sin(values[0][i + 1] - values[0][i]) / mass[i] -
                        g * Math.sin(values[0][i]) - a_x * Math.cos(values[0][i]) - a_y * Math.sin(values[0][i]));
            }
            else if (i == n - 1) {
                result[i] = (float) (-T[i - 1] * Math.sin(values[0][i] - values[0][i - 1]) / mass[i - 1]);
            }
            else {
                result[i] = (float) (-T[i - 1] * Math.sin(values[0][i] - values[0][i - 1]) / mass[i - 1] +
                        T[i + 1] * Math.sin(values[0][i + 1] - values[0][i]) / mass[i]);
            }
            result[i] /= length[i];
        }
        return result;
    }

    private float[][] getValues(float[][] start, float[][] derivative, float delta) {
        float[][] result = new float[2][n];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = start[i][j] + derivative[i][j] * delta;
            }
        }
        return result;
    }

    public Point[] getPoints(int x_0, int y_0, float m2p) {
        Point[] result = new Point[n + 1];
        result[0] = new Point(x_0, y_0);
        for (int i = 0; i < n; i++) {
            result[1 + i] = new Point((int) (length[i] * m2p * Math.sin(parameters[0][i])), (int) (length[i] * m2p * Math.cos(parameters[0][i])));
            result[1 + i].translate(result[i].x, result[i].y);
        }
        return result;
    }

}
