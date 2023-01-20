package com.physics;

public class DoublePendulum {
    private float l1;                   //meters
    private float m1;                     //kg
    private float l2;                   //meters
    private float m2;                     //kg
    private float g;                        //N/kg
    private float[] parameters = new float[]{0, 0, 0, 0}; //theta1, theta2, omega1, omega2

    private long time;
    private long prev_time;

    public DoublePendulum(float theta1, float l1, float m1, float theta2, float l2, float m2, float g) {
        this.parameters[0] = theta1;
        this.parameters[2] = 0;
        this.l1 = l1;
        this.m1 = m1;
        this.parameters[1] = theta2;
        this.parameters[3] = 0;
        this.l2 = l2;
        this.m2 = m2;
        this.g = g;
    }

    public DoublePendulum(float theta1, float l1, float theta2, float length2, float g) {
        this(theta1, l1, 1, theta2, length2, 1, 9.81f);
    }

    public DoublePendulum(float theta1, float theta2) {
        this(theta1, 1, theta2, 1, 9.81f);
    }

    public void initDoublePendulum() {
        prev_time = System.nanoTime();
    }

    public float[] calculateFirstCords() {
        return new float[]{(float) (l1 * Math.sin(parameters[0])), (float) (l1 * Math.cos(parameters[0]))};
    }

    public float[] calculateSecondCords() {
        return new float[]{(float) (l2 * Math.sin(parameters[1])), (float) (l2 * Math.cos(parameters[1]))};
    }

    public void update() {
        time = System.nanoTime();
        float delta = (time - prev_time) / 1000000000f;
        //float delta = 0.01f;

        parameters = calculateParameters(parameters, delta);

        prev_time = time;
    }

    private float calculateBeta1(float[] x_n) {
        double sin1 = Math.sin(x_n[0]);
        double sin2 = Math.sin(x_n[1]);
        double sin3 = Math.sin(x_n[1] - x_n[0]);
        double cos3 = Math.cos(x_n[1] - x_n[0]);

        float result = (float) (m2 * l2 * x_n[3] * x_n[3] * sin3 + m2 * l1 * x_n[2] * x_n[2] * sin3 * cos3 + m2 * g * cos3 * sin2 - (m1 + m2) * g * sin1);
        result /= l1 * (m1 + m2 - m2 * cos3 * cos3);

        return result;
    }

    private float calculateBeta2(float[] x_n) {
        double sin1 = Math.sin(x_n[0]);
        double sin2 = Math.sin(x_n[1]);
        double sin3 = Math.sin(x_n[1] - x_n[0]);
        double cos3 = Math.cos(x_n[1] - x_n[0]);

        float result = (float) ((m1 + m2) * g * sin1 * cos3 - m2 * l2 * x_n[3] * x_n[3] * sin3 * cos3 - (m1 + m2) * g * sin2 - (m1 + m2) * l1 * x_n[2] * x_n[2] * sin3);
        result /= (l2 * (m1 + m2 - m2 * cos3 * cos3));

        return result;
    }

    private float[] getValues(float[] start, float[] derivative, float delta) {
        float[] result = new float[start.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = start[i] + derivative[i] * delta;
        }
        return result;
    }

    private void getDerivatives(float[] derivatives, float[] values) {
        derivatives[0] = values[2];
        derivatives[1] = values[3];
        derivatives[2] = calculateBeta1(values);
        derivatives[3] = calculateBeta2(values);
    }

    private float[] calculateParameters(float[] x_n, float delta) { //x_n - theta1, theta2, omega1, omega2
        float[] a_n = new float[4];
        float[] b_n = new float[4];
        float[] c_n = new float[4];
        float[] d_n = new float[4];

        getDerivatives(a_n, x_n);
        getDerivatives(b_n, getValues(x_n, a_n, delta / 2));
        getDerivatives(c_n, getValues(x_n, b_n, delta / 2));
        getDerivatives(d_n, getValues(x_n, c_n, delta));

        float[] resultDerivative = new float[4];
        for (int i = 0; i < resultDerivative.length; i++) {
            resultDerivative[i] = a_n[i] + 2 * b_n[i] + 2 * c_n[i] + d_n[i];
            resultDerivative[i] /= 6;
        }

        return getValues(x_n, resultDerivative, delta);
    }
}
