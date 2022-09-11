package me.petrolingus.unn.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Algorithm {

    public static double WIDTH = 30.0;
    public static double HEIGHT = 30.0;
    public static double EPSILON = 119.8;
    public static double SIGMA = 3.405;
    public static double N = 1;
    public static double MAX_SPEED = 0.1;
    public static double TAU = 1.82e-12;
    public static double DT = 0.01 * TAU;
    public static double DT2 = DT * DT;
    public static int STEPS = 1000;

    private final List<Particle> particles = new ArrayList<>();

    private final List<List<Particle>> snapshots = new ArrayList<>();

    public Algorithm() {
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < N; i++) {
            double x = ThreadLocalRandom.current().nextDouble(WIDTH);
            double y = ThreadLocalRandom.current().nextDouble(HEIGHT);
            double direction = ThreadLocalRandom.current().nextDouble(2.0 * Math.PI);
            double vx = MAX_SPEED * Math.sin(direction);
            double vy = MAX_SPEED * Math.cos(direction);
            particles.add(new Particle(x, y, vx, vy, 0, 0));
        }
        snapshot();
    }

    public List<List<Particle>> run() {
        for (int i = 0; i < STEPS; i++) {
            iterate();
            snapshot();
        }
        return snapshots;
    }


    public void iterate() {
        for (Particle a : particles) {
            double x = a.x + a.vx * DT + 0.5 * a.ax * DT2;
            double y = a.y + a.vy * DT + 0.5 * a.ay * DT2;
            x = (x < 0) ? (x + WIDTH) : x;
            x = (x > WIDTH) ? (x - WIDTH) : x;
            y = (y < 0) ? (y + HEIGHT) : y;
            y = (y > HEIGHT) ? (y - HEIGHT) : y;
            a.x = x;
            a.y = y;
        }
    }

    private void snapshot() {
        snapshots.add(new ArrayList<>(particles));
    }

}
