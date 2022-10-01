package me.petrolingus.unn.psr.core;

import me.petrolingus.unn.psr.core.generator.ParticleGenerator;
import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class OldAlgorithm extends DefaultAlgorithm {

    List<Particle> particles;
    List<List<Particle>> particleData = new ArrayList<>();

    double pe;
    double ke;

    public OldAlgorithm(ParticleGenerator generator) {
        this.particles = List.of(generator.generate()[0]);
    }

    private void step(double dt, double dt2, int n, double a6, double w, double h, double m, double c) {

        for (Particle a : particles) {
            a.x += a.vx * dt + 0.5 * a.ax * dt2 / m;
            a.y += a.vy * dt + 0.5 * a.ay * dt2 / m;
            a.periodic(w, h);
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt / m;
            a.vy += 0.5 * a.ay * dt / m;
        }

        for (Particle a : particles) {
            a.ax = 0;
            a.ay = 0;
        }

        for (int i = 0; i < n; i++) {
            Particle a = particles.get(i);
            for (int j = i + 1; j < n; j++) {
                Particle b = particles.get(j);
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                dx = (Math.abs(dx) > 0.5 * w) ? dx - w * Math.signum(dx) : dx;
                dy = (Math.abs(dy) > 0.5 * h) ? dy - h * Math.signum(dy) : dy;
                double r2 = dx * dx + dy * dy;

                double r4 = r2 * r2;
                double r6 = r2 * r4;
                double r8 = r4 * r4;

                double force = c * (a6 / r6 - 1.0) / r8;
                double fx = force * dx;
                double fy = force * dy;

                a.ax += fx;
                a.ay += fy;
                b.ax -= fx;
                b.ay -= fy;

                double c2 = a6 / r6;
                pe += Configuration.EPSILON * c2 * (c2 - 2.0);
            }
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt / m;
            a.vy += 0.5 * a.ay * dt / m;
        }

        for (Particle a : particles) {
            ke += (a.vx * a.vx + a.vy * a.vy);
        }
        ke = 0.5 * Configuration.M * ke;
    }

    public double getTemperature() {
        double averageKineticEnergy = 0;
        for (Particle a : particles) {
            averageKineticEnergy += (a.vx * a.vx + a.vy * a.vy);
        }
        averageKineticEnergy = Configuration.M * averageKineticEnergy * 0.5 / (Configuration.K * Configuration.N);
        return averageKineticEnergy;
    }

    @Override
    public void start() {

        double dt = Configuration.DT;
        double dt2 = Configuration.DT2;
        int n = Configuration.N;
        double a6 = Configuration.A6;
        double w = Configuration.WIDTH;
        double h = Configuration.HEIGHT;
        double m = Configuration.M;
        double c = Configuration.C;
        int steps = Configuration.STEPS;

        Timer.start("GENERATION_ANIMATION");
        for (int i = 0; i < steps; i++) {
            List<Particle> data = new ArrayList<>(n);
            for (Particle particle : particles) {
                data.add(new Particle(particle.x, particle.y, 0, 0));
            }
            particleData.add(data);

            step(dt, dt2, n, a6, w, h, m, c);

            temperatureList.add(getTemperature());

            pe /= (n * Configuration.eV);
            ke /= (n * Configuration.eV);
            double fullEnergy = (pe + ke);

            fullEnergyList.add(fullEnergy);

            potentialEnergyList.add(pe);
            pe = 0;

            kineticEnergyList.add(ke);
            ke = 0;

        }
        Timer.measure("GENERATION_ANIMATION");

        RuntimeConfiguration.maxFrame = particleData.size();
        DefaultAlgorithm.done = true;
    }

    public List<Particle> getParticleData(int index) {
        return particleData.get(index);
    }
}