package com.physics;

public class Pendulum {
    private float theta;                    //rad
    private float omega;                    //rad/sec
    private float length;                   //meters
    private float mass;                     //kg
    private float g;                        //N/kg
    private float k;                        //kg/sec

    private long time;
    private long prev_time;
    private long period_time_1;
    private long period_time_2;
    private float omega_prev;

    public Pendulum(float theta, float length, float mass, float g, float k) {
        this.theta = theta;
        this.omega = 0;
        this.length = length;
        this.mass = mass;
        this.g = g;
        this.k = k;
    }

    public Pendulum(float theta, float length, float mass) {
        this(theta, length, mass, 9.81f, 1);
    }

    public Pendulum(float theta) {
        this(theta, 1, 1, 9.81f, 0.0f);
    }

    public float getTheta() {
        return theta;
    }

    public void initPendulum() {
        prev_time = System.nanoTime();
        period_time_1 = prev_time;
    }

    public float[] calculateCoords() {
        return new float[]{(float) (length * Math.sin(theta)), (float) (length * Math.cos(theta))};
    }

    public void update() {
        time = System.nanoTime();
        theta += omega * (time - prev_time) / 2000000000f;
        omega_prev = omega;
        omega -= (g * Math.sin(theta) / length + k * omega / mass)  * (time - prev_time) / 1000000000f;
        if (omega_prev * omega <= 0) {
            System.out.println(time - period_time_1);
            period_time_1 = time;
        }
        theta += omega * (time - prev_time) / 2000000000f;
        prev_time = time;
    }
}
