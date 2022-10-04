package me.petrolingus.unn.psr.core.generator;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.model.Particle;
import me.petrolingus.unn.psr.core.Timer;

import java.util.concurrent.ThreadLocalRandom;

public class ParticleGenerator {

    public Particle[][] generate() {

        Timer.start("GENERATION_PARTICLES");

        final int STEPS = Configuration.STEPS;
        final int N = Configuration.N;
        final double WIDTH = Configuration.WIDTH;
        final double HEIGHT = Configuration.HEIGHT;
        final double MAX_SPEED = Configuration.MAX_SPEED;
        final double TWO_PI = 2.0 * Math.PI;
        final double R0 = Configuration.R0;

        Particle[][] particles = new Particle[STEPS][N];

        for (int i = 0; i < STEPS; i++) {
            for (int j = 0; j < N; j++) {
                particles[i][j] = new Particle();
            }
        }

        for (int i = 0; i < N; i++) {

            double x;
            double y;

            int att = 100;
            double min = 1.5;

            while (true) {
                x = ThreadLocalRandom.current().nextDouble(WIDTH);
                y = ThreadLocalRandom.current().nextDouble(HEIGHT);
                boolean pass = true;
                for (int j = 0; j < N; j++) {
                    Particle particle = particles[0][j];
                    double dx = x - particle.x;
                    double dy = y - particle.y;
                    dx = (Math.abs(dx) > 0.5 * WIDTH) ? dx - WIDTH * Math.signum(dx) : dx;
                    dy = (Math.abs(dy) > 0.5 * HEIGHT) ? dy - HEIGHT * Math.signum(dy) : dy;
                    double r = Math.sqrt(dx * dx + dy * dy);
                    if (r < min * R0) {
                        pass = false;
                        break;
                    }
                }
                if (att-- == 0) {
                    att = 100;
                    min -= 0.1;
                }
                if (pass) {
                    break;
                }
            }

            double speed = MAX_SPEED > 0 ? ThreadLocalRandom.current().nextDouble(MAX_SPEED) : 0;
            double direction = ThreadLocalRandom.current().nextDouble(TWO_PI);
            double vx = speed * Math.cos(direction);
            double vy = speed * Math.sin(direction);

            particles[0][i] = new Particle(x, y, vx, vy);
        }

        makeSystemPulseAsZero(particles);

        Timer.measure("GENERATION_PARTICLES");

        return particles;
    }

    private void makeSystemPulseAsZero(Particle[][] particles) {

        double sumVx = 0;
        double sumVy = 0;
        for (Particle particle : particles[0]) {
            sumVx += particle.vx;
            sumVy += particle.vy;
        }

        sumVx /= particles[0].length;
        sumVy /= particles[0].length;

        for (Particle particle : particles[0]) {
            particle.vx -= sumVx;
            particle.vy -= sumVy;
        }
    }
}
