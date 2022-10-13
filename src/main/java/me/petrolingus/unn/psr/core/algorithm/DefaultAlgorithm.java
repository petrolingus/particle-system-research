package me.petrolingus.unn.psr.core.algorithm;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.generator.ParticleGenerator;
import me.petrolingus.unn.psr.core.model.Particle;

import java.util.List;

public class DefaultAlgorithm extends Algorithm {

    List<Particle> particles;

    double pe;
    double ke;

    public DefaultAlgorithm(ParticleGenerator generator) {
        this.particles = List.of(generator.generate()[0]);
        step = 0;
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

                // Calculate potential energy of particle system in Joules
                double c2 = a6 / r6;
                pe += Configuration.EPSILON * c2 * (c2 - 2.0) / Configuration.eV;
            }
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt / m;
            a.vy += 0.5 * a.ay * dt / m;
        }

        // Calculate kinetic energy of particle system in Joules
        for (Particle a : particles) {
            double v2 = a.vx * a.vx + a.vy * a.vy;
            ke += 0.5 * Configuration.M * v2 / Configuration.eV;
            a.mv2 += Configuration.M * v2;
        }
    }

    public void next() {
        double dt = Configuration.DT;
        double dt2 = Configuration.DT2;
        int n = Configuration.N;
        double a6 = Configuration.A6;
        double w = Configuration.WIDTH;
        double h = Configuration.HEIGHT;
        double m = Configuration.M;
        double c = Configuration.C;
        step(dt, dt2, n, a6, w, h, m, c);
        step++;

        if (step < Configuration.T_MAX_STEPS && step % Configuration.T_RECALCULATE_VELOCITY_STEP == 0) {
            double temp = particles.stream().mapToDouble(e -> e.mv2 / Configuration.T_RECALCULATE_VELOCITY_STEP).sum();
            double beta = Math.sqrt(2.0 * n * Configuration.K * Configuration.INIT_T / temp);
            for (Particle p : particles) {
                p.vx *= beta;
                p.vy *= beta;
                p.mv2 = 0;
            }
        }
    }

    public void snapshot() {
        particleList = particles.subList(0, particles.size());

        potential = pe / (Configuration.N * Configuration.NSNAP);
        kinetic = ke / (Configuration.N * Configuration.NSNAP);
        full = (pe + ke) / (Configuration.N * Configuration.NSNAP);
        temperature = (ke * Configuration.eV) / (Configuration.N * Configuration.NSNAP * Configuration.K);

        temperatureList.add(temperature);

        pe = 0;
        ke = 0;
    }
}