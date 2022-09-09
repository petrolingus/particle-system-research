package me.petrolingus.unn.psr.core;

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

        int particleCount = Configuration.particleCount;
        double particleSize = Configuration.particleSize;
        int width = Configuration.Lx;
        int height = Configuration.Ly;

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
                    double d = getSquareDistance(dx, dy);
                    if (d < 2 * particleSize) {
                        isOkay = false;
                        break;
                    }
                }
                if (isOkay) {
                    break;
                }
            }
            double maxSpeed = Configuration.maxSpeed;
            double speed = ThreadLocalRandom.current().nextDouble(-maxSpeed, maxSpeed);
            double direction = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
            double vx = speed * Math.cos(direction);
            double vy = speed * Math.sin(direction);
            particles.add(new Particle(x, y, vx, vy));
        }
        System.out.printf("Generate %d particles\n", particleCount);

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

        double dt = Configuration.dt;
        double dt2 = Configuration.dt2;
        int particleCount = Configuration.particleCount;
        double a6 = Configuration.a6;
        double c = Configuration.c;

        for (Particle a : particles) {
            double newX = a.x + a.vx * dt + 0.5 * a.ax * dt2;
            double newY = a.y + a.vy * dt + 0.5 * a.ay * dt2;
            double[] periodic = periodic(newX, newY);
            a.x = periodic[0];
            a.y = periodic[1];
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt;
            a.vy += 0.5 * a.ay * dt;
        }

        for (int i = 0; i < particleCount; i++) {
            Particle a = particles.get(i);
            double ax = 0;
            double ay = 0;
            for (int j = 0; j < particleCount; j++) {
                if (i == j) continue;
                Particle b = particles.get(j);
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double r = getSquareDistance(dx, dy);
                double c1 = r * r;
                double c2 = (a6 / (c1 * r)) - 1.0;
                double c3 = c1 * c1;
                ax += c2 * (dx / c3);
                ay += c2 * (dy / c3);
            }
            a.ax = c * ax;
            a.ay = c * ay;
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt;
            a.vy += 0.5 * a.ay * dt;
        }
    }

    public double getSquareDistance(double dx, double dy) {
        double width = Configuration.Lx;
        double height = Configuration.Ly;
        dx = (Math.abs(dx) > 0.5 * width) ? dx - width * Math.signum(dx) : dx;
        dy = (Math.abs(dy) > 0.5 * height) ? dy - height * Math.signum(dy) : dy;
        return dx * dx + dy * dy;
    }

    public double getAverageKineticEnergy() {
        double averageKineticEnergy = 0;
        for (Particle a : particles) {
            averageKineticEnergy += Math.sqrt(a.vx * a.vx + a.vy * a.vy) / 2.0;
        }
        if (beginKineticEnergy == -1) {
            beginKineticEnergy = averageKineticEnergy;
        }
        return averageKineticEnergy;
    }

    public double[] periodic(double x, double y) {
        double width = Configuration.Lx;
        double height = Configuration.Ly;
        x = (x < 0) ? (x + width) : x;
        x = (x > width) ? (x - width) : x;
        y = (y < 0) ? (y + height) : y;
        y = (y > height) ? (y - height) : y;
        return new double[]{x, y};
    }

    public void start() {
        int particleCount = Configuration.particleCount;
        int steps = Configuration.countOfSteps;
        Timer.start("GENERATION_ANIMATION");
        for (int i = 0; i < steps; i++) {
            List<ParticleData> data = new ArrayList<>(particleCount);
            for (Particle particle : particles) {
                data.add(new ParticleData(particle.x, particle.y));
            }
            particleData.add(data);
            double currentKineticEnergy = getAverageKineticEnergy();
            averageKineticEnergyList.add(currentKineticEnergy);
            if (currentKineticEnergy / beginKineticEnergy > 50) {
                break;
            }
            run();
        }
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
