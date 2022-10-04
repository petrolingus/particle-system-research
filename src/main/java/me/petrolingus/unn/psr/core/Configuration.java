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

    public static double C;

    public static int NSNAP;

    public static double INIT_T;
    public static double T_MAX_STEPS;
    public static double T_RECALCULATE_VELOCITY_STEP;

    public static void recalculate() {
        EPSILON = 0.0103 * 1.602176634e-19; // eV
        R0 = 0.382; // nm

        A6 = Math.pow(R0, 6);

        WIDTH = HEIGHT = 30.0 * R0; // nm

        N = 100;
        MAX_SPEED = 300; // m/s

        TAU = 1.82e-3; // ns
        DT = 0.01 * TAU;
        DT2 = DT * DT;

        STEPS = 100_000;

        M = 66.3352146e-27;
        K = 1.380649e-23; // J/Kelvin
        eV = 1.602176634e-19;
        C = 12.0 * EPSILON * A6;

        NSNAP = 4;

        // Перенормировка скоростей
        T_MAX_STEPS = 50000;
        T_RECALCULATE_VELOCITY_STEP = 10;
        INIT_T = 10;
        MAX_SPEED = Math.sqrt(2.0 * K * INIT_T / M); // 0.5mv^2 = nkT;
    }

}
