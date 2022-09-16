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

//        int particleCount = Configuration.N;
//        double particleSize = Configuration.PARTICLE_RADIUS;
//        int width = Configuration.WIDTH;
//        int height = Configuration.HEIGHT;

//        for (int i = 0; i < particleCount; i++) {
//            double x;
//            double y;
////            while (true) {
////                x = ThreadLocalRandom.current().nextDouble(width);
////                y = ThreadLocalRandom.current().nextDouble(height);
////                boolean isOkay = true;
////                for (Particle p : particles) {
////                    double dx = p.x - x;
////                    double dy = p.y - y;
////                    double d = getSquareDistance(dx, dy);
////                    if (d < 2 * Configuration.R0) {
////                        isOkay = false;
////                        break;
////                    }
////                }
////                if (isOkay) {
////                    break;
////                }
////            }
//
//            double d = Configuration.WIDTH * 0.2;
//            x = Configuration.WIDTH / 2.0 + d * Math.pow(-1, i + 1);
//            y = Configuration.WIDTH / 2.0;
//
//            double maxSpeed = Configuration.MAX_SPEED;
//            double speed = ThreadLocalRandom.current().nextDouble(0, maxSpeed);
//            double direction = ThreadLocalRandom.current().nextDouble(2 * Math.PI);
//            double vx = speed * Math.cos(direction);
//            double vy = speed * Math.sin(direction);
//
//            vx = 0;
//            vy = 0;
//
//            particles.add(new Particle(x, y, vx, vy));
//        }


        particles.add(new Particle(Configuration.WIDTH / 2.0 + Configuration.R0, Configuration.WIDTH / 2.0, 0, 0));
        particles.add(new Particle(Configuration.WIDTH / 2.0 - Configuration.R0, Configuration.WIDTH / 2.0, 0, 0));

        System.out.printf("Generate %d particles\n", particles.size());

        // Изменяем суммарный импульс системы, чтобы он был равен нулю
//        double sumVx = 0;
//        double sumVy = 0;
//        for (Particle particle : particles) {
//            sumVx += particle.vx;
//            sumVy += particle.vy;
//        }
//        sumVx /= particleCount;
//        sumVy /= particleCount;
//        for (Particle particle : particles) {
//            particle.vx -= sumVx;
//            particle.vy -= sumVy;
//        }

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

        for (Particle a : particles) {
            double newX = a.x + a.vx * dt + 0.5 * a.ax * dt2;
            double newY = a.y + a.vy * dt + 0.5 * a.ay * dt2;
            double[] periodic = periodic(newX, newY, width, height);
            a.x = periodic[0];
            a.y = periodic[1];
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt;
            a.vy += 0.5 * a.ay * dt;
        }

        for (int i = 0; i < particleCount; i++) {
            Particle a = particles.get(i);
            for (int j = i + 1; j < particleCount; j++) {
                Particle b = particles.get(j);
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double r = getSquareDistance(dx, dy, width, height);

                double force = 12.0 * D * a6 * (a6 / Math.pow(r, 6) - 1.0) / Math.pow(r, 8);

                a.ax += force * dx;
                a.ay += force * dy;
                b.ax -= force * dx;
                b.ay -= force * dy;
            }
        }

        for (Particle a : particles) {
            a.vx += 0.5 * a.ax * dt;
            a.vy += 0.5 * a.ay * dt;
        }
    }

    public double getSquareDistance(double dx, double dy, double width, double height) {
        dx = (Math.abs(dx) > 0.5 * width) ? dx - width * Math.signum(dx) : dx;
        dy = (Math.abs(dy) > 0.5 * height) ? dy - height * Math.signum(dy) : dy;
        return Math.sqrt(dx * dx + dy * dy);
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
            List<ParticleData> data = new ArrayList<>(particleCount);
            for (Particle particle : particles) {
                data.add(new ParticleData(particle.x, particle.y));
            }
            particleData.add(data);
            double currentKineticEnergy = getAverageKineticEnergy();
            averageKineticEnergyList.add(currentKineticEnergy);
            RuntimeConfiguration.maxFrame = steps;
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
