package me.petrolingus.unn.test;

import me.petrolingus.unn.psr.core.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Algorithm {

    public static double WIDTH = 30.0;
    public static double HEIGHT = 30.0;
    public static double EPSILON = 119.8;
    public static double SIGMA = 3.405;
    public static double N = 25;
    public static double MAX_SPEED = 10 * 1e9;
    public static double TAU = 1.82e-12;
    public static double DT = 0.01 * TAU;
    public static double DT2 = DT * DT;
    public static int STEPS = 1_000_000;

    public static double SIGMA6 = Math.pow(SIGMA, 6);
    public static double CONSTANT = 12.0 * EPSILON * SIGMA6;

    private final List<Particle> particles = new ArrayList<>();

    private final List<List<Particle>> snapshots = new ArrayList<>();

    public Algorithm() {
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < N; i++) {
            double x = ThreadLocalRandom.current().nextDouble(WIDTH);
            double y = ThreadLocalRandom.current().nextDouble(HEIGHT);
            while (true) {
                x = ThreadLocalRandom.current().nextDouble(WIDTH);
                y = ThreadLocalRandom.current().nextDouble(HEIGHT);
                boolean isOkay = true;
                for (Particle p : particles) {
                    double dx = p.x - x;
                    double dy = p.y - y;
                    double d = getSquareDistance(dx, dy);
                    if (d < SIGMA) {
                        isOkay = false;
                        break;
                    }
                }
                if (isOkay) {
                    break;
                }
            }
            double direction = ThreadLocalRandom.current().nextDouble(2.0 * Math.PI);
            double vx = MAX_SPEED * Math.cos(direction);
            double vy = MAX_SPEED * Math.sin(direction);
            particles.add(new Particle(x, y, vx, vy, 0, 0));
        }
        // Изменяем суммарный импульс системы, чтобы он был равен нулю
        double sumVx = 0;
        double sumVy = 0;
        for (Particle particle : particles) {
            sumVx += particle.vx;
            sumVy += particle.vy;
        }
        sumVx /= N;
        sumVy /= N;
        for (Particle particle : particles) {
            particle.vx -= sumVx;
            particle.vy -= sumVy;
        }
        snapshot();
    }

    public List<List<Particle>> run() {
        for (int i = 0; i < STEPS; i++) {
            iterate();
            if (i % 500 == 0) {
                snapshot();
            }
        }
        List<List<Particle>> list = new ArrayList<>();
        for (List<Particle> l : snapshots) {
            list.add(l);
        }
        return list;
    }

    private double getSquareDistance(double dx, double dy) {
        dx = (Math.abs(dx) > 0.5 * WIDTH) ? dx - WIDTH * Math.signum(dx) : dx;
        dy = (Math.abs(dy) > 0.5 * HEIGHT) ? dy - HEIGHT * Math.signum(dy) : dy;
        return dx * dx + dy * dy;
    }


    public void iterate() {
        for (Particle p : particles) {
            double x = p.x + p.vx * DT + 0.5 * p.ax * DT2;
            double y = p.y + p.vy * DT + 0.5 * p.ay * DT2;
//            double x = p.x + p.vx * 1;
//            double y = p.y + p.vy * 1;
            x = (x < 0) ? (x + WIDTH) : x;
            x = (x > WIDTH) ? (x - WIDTH) : x;
            y = (y < 0) ? (y + HEIGHT) : y;
            y = (y > HEIGHT) ? (y - HEIGHT) : y;
            p.x = x;
            p.y = y;
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * DT;
            a.vy += 0.5 * a.ay * DT;
        }

        for (int i = 0; i < N; i++) {
            Particle a = particles.get(i);
            double ax = 0;
            double ay = 0;
            for (int j = 0; j < N; j++) {
                if (i == j) continue;
                Particle b = particles.get(j);
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double r = getSquareDistance(dx, dy);
                double c1 = r * r;
                double c2 = (SIGMA6 / (c1 * r)) - 1.0;
                double c3 = c1 * c1;
                ax += c2 * (dx / c3);
                ay += c2 * (dy / c3);
            }
            a.ax = CONSTANT * ax;
            a.ay = CONSTANT * ay;
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * DT;
            a.vy += 0.5 * a.ay * DT;
        }

    }

    private void snapshot() {
        List<Particle> parti = new ArrayList<>();
        for (Particle p : particles) {
            Particle particle = new Particle(p.x, p.y, p.vx, p.vy, 0, 0);
            parti.add(particle);
        }
        snapshots.add(parti);
    }

}
