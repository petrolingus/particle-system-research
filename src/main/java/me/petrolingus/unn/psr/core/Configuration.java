package me.petrolingus.unn.psr.core;

public class Configuration {

    public static int WIDTH = 30;
    public static int HEIGHT = 30;

    public static int N = 100;
    public static double MAX_SPEED = 10 * 1e9;

    public static double SIGMA = 3.405;
    public static double EPSILON = 119.8;
    public static double SIGMA6 = Math.pow(SIGMA, 6);
    public static double CONSTANT = 12 * EPSILON * SIGMA6;

    public static double TAU = 1.82e-12;
    public static double DT = 0.01 * TAU;
    public static double DT2 = DT * DT;

    public static int STEPS = 100_000;

    public static double particleSize = WIDTH / 130.0; // diameter

    public static void recalculate() {
        SIGMA6 = Math.pow(SIGMA, 6);
        CONSTANT = 12 * EPSILON * SIGMA6;
        DT = DT * TAU;
        DT2 = DT * DT;
    }

}
