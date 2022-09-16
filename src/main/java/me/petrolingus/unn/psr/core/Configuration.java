package me.petrolingus.unn.psr.core;

public class Configuration {

    public static int WIDTH = 0;
    public static int HEIGHT = 0;

    public static int N = 100;
    public static double MAX_SPEED = 10 * 1e9;

    public static double SIGMA = 0;
    public static double EPSILON = 0;
    public static double SIGMA6 = 0;
    public static double CONSTANT = 0;
    public static double R0 = 0;

    public static double TAU = 0;
    public static double DT = 0;
    public static double DT2;

    public static int STEPS;

    public static double particleRadius = 0;

    public static void recalculate() {
        R0 = Math.pow(2, 1.0 / 6.0) * SIGMA;
        SIGMA6 = Math.pow(R0, 6);
        CONSTANT = 12 * EPSILON * SIGMA6;
        DT = DT * TAU;
        DT2 = DT * DT;
        particleRadius = R0 / 2.0;
        WIDTH = (int) Math.round(30.0 * R0);
        HEIGHT = (int) Math.round(30.0 * R0);
    }

}
