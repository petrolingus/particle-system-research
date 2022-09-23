package me.petrolingus.unn.psr.core;

public class Configuration {

    public static double WIDTH;
    public static double HEIGHT;

    public static int N;
    public static double MAX_SPEED;

    public static double EPSILON;
    public static double A6;
    public static double R0;

    public static double TAU;
    public static double DT;
    public static double DT2;

    public static int STEPS;
    public static double M;

    public static double K;
    public static double eV;

    public static double PARTICLE_RADIUS;

    public static void recalculate() {
//        R0 = Math.pow(2, 1.0 / 6.0) * SIGMA;
//        A6 = Math.pow(R0, 6);
//        CONSTANT = 12.0 * EPSILON * A6;
//        DT = 0.01 * TAU;
//        DT2 = DT * DT;
//        PARTICLE_RADIUS = R0 / 2.0;
//        WIDTH = (int) Math.round(30.0 * R0);
//        HEIGHT = (int) Math.round(30.0 * R0);
//        N = 2;

        // pow(2, 1/6) = 1.12246204831

        EPSILON = 0.0103 * 1.602176634e-19; // eV
        R0 = 0.382; // nm

        A6 = Math.pow(R0, 6);

        WIDTH = HEIGHT = 30.0 * R0; // nm

        N = 100;
        MAX_SPEED = 600; // nm/ns

        TAU = 1.82e-3; // ns
        DT = 0.01 * TAU;
        DT2 = DT * DT;

        STEPS = 200_000;

        M = 66.3352146e-27;
        K = 1.380649e-23; // J/Kelvin
        eV = 1.602176634e-19;
    }

}
