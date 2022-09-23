package me.petrolingus.unn.psr.core;

import me.petrolingus.unn.psr.core.generator.ParticleGenerator;

public class Test {

    public static void main(String[] args) {

        Configuration.recalculate();
        ParticleGenerator particleGenerator = new ParticleGenerator();
        Algorithm algorithm = new Algorithm(particleGenerator);
        algorithm.start();

        for (int i = 0; i < 3; i++) {
            System.out.println(i + ":");
            for (Particle particle : algorithm.getParticleData(i)) {
                System.out.println(particle);
            }
            System.out.println("===================================");
        }

    }
}
