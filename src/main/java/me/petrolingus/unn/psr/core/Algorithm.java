package me.petrolingus.unn.psr.core;

import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Algorithm {

    private double beginKineticEnergy = -1;

    private static boolean done = false;

    List<Particle> particles = new ArrayList<>();

    public static List<Double> averageKineticEnergyList = new ArrayList<>();
    List<List<ParticleData>> particleData = new ArrayList<>();

    public Algorithm() {
        initialize();
    }

    private void initialize() {

        int particleCount = Configuration.N;
        double particleSize = Configuration.PARTICLE_RADIUS;
        double width = Configuration.WIDTH;
        double height = Configuration.HEIGHT;

        for (int i = 0; i < particleCount; i++) {
            double x;
            double y;
            while (true) {
                x = ThreadLocalRandom.current().nextDouble(width);
                y = ThreadLocalRandom.current().nextDouble(height);
                boolean isOkay = true;
                for (Particle p : particles) {
                    double dx = p.x - x;
                    double dy = p.y - y;
                    double d = Math.sqrt(getSquareDistance(dx, dy, width, height));
                    if (d < 1.1 * Configuration.R0) {
                        isOkay = false;
                        break;
                    }
                }
                if (isOkay) {
                    break;
                }
            }

            double maxSpeed = Configuration.MAX_SPEED;
            double speed = ThreadLocalRandom.current().nextDouble(maxSpeed);
            double direction = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
            double vx = speed * Math.cos(direction);
            double vy = speed * Math.sin(direction);

            particles.add(new Particle(x, y, vx, vy));
        }


        // TEST CASES
//        double d = 2;
//        double shift = Configuration.WIDTH / 6.0;
//        particles.add(new Particle(Configuration.WIDTH / 2.0 + d * Configuration.R0 - shift, Configuration.WIDTH / 2.0, 0, 0));
//        particles.add(new Particle(Configuration.WIDTH / 2.0 - d * Configuration.R0 - shift, Configuration.WIDTH / 2.0, 0, 0));
//        particles.add(new Particle(Configuration.WIDTH / 2.0, Configuration.WIDTH / 2.0 + d * Configuration.R0 - shift, 0, 0));
//        particles.add(new Particle(Configuration.WIDTH / 2.0, Configuration.WIDTH / 2.0  - d * Configuration.R0 - shift, 0, 0));

        System.out.printf("Generate %d particles\n", particles.size());

        // Изменяем суммарный импульс системы, чтобы он был равен нулю
        double sumVx = 0;
        double sumVy = 0;
        for (Particle particle : particles) {
            sumVx += particle.vx;
            sumVy += particle.vy;
        }
        sumVx /= particleCount;
        sumVy /= particleCount;
        for (Particle particle : particles) {
            particle.vx -= sumVx;
            particle.vy -= sumVy;
        }

        Timer.start("LOG_SUM_ENERGY");
    }

    public void run() {

        double dt = Configuration.DT;
        double dt2 = Configuration.DT2;
        int particleCount = particles.size();
        double a6 = Configuration.A6;
        double D = Configuration.EPSILON;
        double width = Configuration.WIDTH;
        double height = Configuration.HEIGHT;
        double m = 66.3352146e-27;
        double c = 12.0 * D * a6;

        for (Particle a : particles) {
            double newX = a.x + a.vx * dt + 0.5 * a.ax * dt2 / m;
            double newY = a.y + a.vy * dt + 0.5 * a.ay * dt2 / m;
            double[] periodic = periodic(newX, newY, width, height);
            a.x = periodic[0];
            a.y = periodic[1];
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt / m;
            a.vy += 0.5 * a.ay * dt / m;
        }

        for (Particle a : particles) {
            a.ax = 0;
            a.ay = 0;
        }

        for (int i = 0; i < particleCount; i++) {
            Particle a = particles.get(i);
            for (int j = i + 1; j < particleCount; j++) {
                Particle b = particles.get(j);
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

                a.ax += fx;
                a.ay += fy;
                b.ax -= fx;
                b.ay -= fy;

            }
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt / m;
            a.vy += 0.5 * a.ay * dt / m;
        }
    }

    public double getSquareDistance(double dx, double dy, double width, double height) {
        dx = (Math.abs(dx) > 0.5 * width) ? dx - width * Math.signum(dx) : dx;
        dy = (Math.abs(dy) > 0.5 * height) ? dy - height * Math.signum(dy) : dy;
        return dx * dx + dy * dy;
    }

    public double getAverageKineticEnergy() {
        double averageKineticEnergy = 0;
        for (Particle a : particles) {
            averageKineticEnergy += (a.vx * a.vx + a.vy * a.vy) / 2.0;
        }
        if (beginKineticEnergy == -1) {
            beginKineticEnergy = averageKineticEnergy;
        }
        return averageKineticEnergy;
    }

    public double[] periodic(double x, double y, double width, double height) {
        x = (x < 0) ? (x + width) : x;
        x = (x > width) ? (x - width) : x;
        y = (y < 0) ? (y + height) : y;
        y = (y > height) ? (y - height) : y;
        return new double[]{x, y};
    }

    public void start() {
        int particleCount = Configuration.N;
        int steps = Configuration.STEPS;
        Timer.start("GENERATION_ANIMATION");
        for (int i = 0; i < steps; i++) {
            if (i % 1 == 0) {
                List<ParticleData> data = new ArrayList<>(particleCount);
                for (Particle particle : particles) {
                    data.add(new ParticleData(particle.x, particle.y));
                }
                particleData.add(data);
                averageKineticEnergyList.add(getAverageKineticEnergy());
            }
            run();
        }
        RuntimeConfiguration.maxFrame = particleData.size();
        Timer.measure("GENERATION_ANIMATION");
        done = true;
    }

    public List<ParticleData> getParticleData(int index) {
        return particleData.get(index);
    }

    public static List<Double> getAverageKineticEnergyList() {
        return averageKineticEnergyList;
    }

    public static boolean isDone() {
        return done;
    }
}
