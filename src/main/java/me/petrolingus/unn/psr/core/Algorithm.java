package me.petrolingus.unn.psr.core;

import me.petrolingus.unn.psr.core.generator.ParticleGenerator;
import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;

import java.util.List;

public class Algorithm extends DefaultAlgorithm {

    private final Particle[][] particles;

    public Algorithm(ParticleGenerator generator) {
        this.particles = generator.generate();
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

        for (int i = 0; i < particleCount; i++) {
            particles[k + 1][i].ax = 0;
            particles[k + 1][i].ay = 0;
        }

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

                double force = c * (a6 / r3 - 1.0) / r4;
                double fx = force * dx;
                double fy = force * dy;

                particles[k + 1][i].ax += fx;
                particles[k + 1][i].ay += fy;
                particles[k + 1][j].ax -= fx;
                particles[k + 1][j].ay -= fy;
            }
        }

        for (int i = 0; i < particleCount; i++) {
            particles[k + 1][i].vx += +0.5 * particles[k + 1][i].ax * dt / m;
            particles[k + 1][i].vy += +0.5 * particles[k + 1][i].ay * dt / m;
        }
//
//        for (Particle a : particles) {
//            double k = Configuration.M * (a.vx * a.vx + a.vy * a.vy) / 2.0;
//            vvsum += k;
//            ke += k;
//        }
    }

    public double getAverageKineticEnergy(int i) {
        double averageKineticEnergy = 0;
        for (Particle a : particles[i]) {
            averageKineticEnergy += (a.vx * a.vx + a.vy * a.vy);
        }
        averageKineticEnergy = Configuration.M * averageKineticEnergy * 0.5 / (Configuration.K * Configuration.N);
        return averageKineticEnergy;
    }

    @Override
    public void start() {

        final int STEPS = Configuration.STEPS;
        Timer.start("GENERATION_ANIMATION");
        for (int i = 0; i < STEPS - 1; i++) {
            if (i > 50_000) {
                temperatureList.add(getAverageKineticEnergy(i));
            }
            run(i);
        }
        RuntimeConfiguration.maxFrame = STEPS;
        Timer.measure("GENERATION_ANIMATION");
        done = true;
    }

    public List<Particle> getParticleData(int index) {
        return List.of(particles[index]);
    }
}
