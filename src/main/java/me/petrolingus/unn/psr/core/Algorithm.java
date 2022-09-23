package me.petrolingus.unn.psr.core;

import me.petrolingus.unn.psr.core.generator.ParticleGenerator;
import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private static boolean done = false;

    public static List<Double> kineticEnergyList = new ArrayList<>();
    public static List<Double> potentialEnergyList = new ArrayList<>();

    private final Particle[][] particles;
    private double ke;
    private double pe;

    public Algorithm(ParticleGenerator generator) {
        this.particles = generator.generate();
        this.ke = 0;
        this.pe = 0;
    }

    public void run(int k) {

        double dt = Configuration.DT;
        double dt2 = Configuration.DT2;
        int particleCount = Configuration.N;
        double a6 = Configuration.A6;
        double D = Configuration.EPSILON;
        double width = Configuration.WIDTH;
        double height = Configuration.HEIGHT;
        double m = Configuration.M;
        double c = 12.0 * D * a6;

        for (int i = 0; i < particleCount; i++) {
            Particle particle = particles[k][i];
            particles[k + 1][i].x = particle.x + particle.vx * dt + 0.5 * particle.ax * dt2 / m;
            particles[k + 1][i].y = particle.y + particle.vy * dt + 0.5 * particle.ay * dt2 / m;
            particles[k + 1][i].periodic(width, height);
        }

        for (int i = 0; i < particleCount; i++) {
            particles[k + 1][i].vx = particles[k][i].vx + 0.5 * particles[k][i].ax * dt / m;
            particles[k + 1][i].vy = particles[k][i].vy + 0.5 * particles[k][i].ay * dt / m;
        }

//        for (int i = 0; i < particleCount; i++) {
//            particles[k + 1][i].ax = 0;
//            particles[k + 1][i].ay = 0;
//        }

        for (int i = 0; i < particleCount; i++) {
            Particle a = particles[k][i];
            for (int j = i + 1; j < particleCount; j++) {
                Particle b = particles[k][j];
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                dx = (Math.abs(dx) > 0.5 * width) ? dx - width * Math.signum(dx) : dx;
                dy = (Math.abs(dy) > 0.5 * height) ? dy - height * Math.signum(dy) : dy;
                double r = dx * dx + dy * dy;

                double r2 = r * r;
                double r3 = r * r2;
                double r4 = r2 * r2;
//                double r6 = r3 * r3;

                double force = c * (a6 / r3 - 1.0) / r4;
                double fx = force * dx;
                double fy = force * dy;

//                double c2 = a6 / r6;
//                pe += D * c2 * (c2 - 2);

                particles[k][i].ax += fx;
                particles[k][i].ay += fy;
                particles[k][j].ax -= fx;
                particles[k][j].ay -= fy;
            }
        }

        for (int i = 0; i < particleCount; i++) {
            particles[k + 1][i].vx += + 0.5 * particles[k][i].ax * dt / m;
            particles[k + 1][i].vy += + 0.5 * particles[k][i].ay * dt / m;
        }
//
//        for (Particle a : particles) {
//            double k = Configuration.M * (a.vx * a.vx + a.vy * a.vy) / 2.0;
//            vvsum += k;
//            ke += k;
//        }
    }

    public double getSquareDistance(double dx, double dy, double width, double height) {
        dx = (Math.abs(dx) > 0.5 * width) ? dx - width * Math.signum(dx) : dx;
        dy = (Math.abs(dy) > 0.5 * height) ? dy - height * Math.signum(dy) : dy;
        return dx * dx + dy * dy;
    }

    public double getKineticEnergy(int steps) {
        return ke / steps;
    }

    public double getPotentialEnergy(int steps) {
        return pe / steps;
    }



    public void start() {

        int particleCount = Configuration.N;
        final int STEPS = Configuration.STEPS;
        Timer.start("GENERATION_ANIMATION");
        for (int i = 0; i < STEPS - 1; i++) {
            run(i);
        }
        RuntimeConfiguration.maxFrame = STEPS;
        Timer.measure("GENERATION_ANIMATION");
        done = true;
    }

    public Particle[] getParticleData(int index) {
        return particles[index];
    }

    public static List<Double> getKineticEnergyList() {
        return kineticEnergyList;
    }

    public static List<Double> getPotentialEnergyList() {
        return potentialEnergyList;
    }

    public static boolean isDone() {
        return done;
    }
}
