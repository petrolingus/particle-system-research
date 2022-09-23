package me.petrolingus.unn.psr.core.generator;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.Particle;

public class SimpleGenerator {

    public static Particle[][] generate() {

        final int STEPS = Configuration.STEPS;
        final double WIDTH = Configuration.WIDTH;
        final double HEIGHT = Configuration.HEIGHT;
        final double R0 = Configuration.R0;

        Particle[][] particles = new Particle[STEPS][4];

        double d = 2;
        double shift = Configuration.WIDTH / 6.0;
        particles[0][1] = new Particle(WIDTH / 2.0 + d * R0 - shift, HEIGHT / 2.0, 0, 0);
        particles[0][2] = new Particle(WIDTH / 2.0 - d * R0 - shift, HEIGHT / 2.0, 0, 0);
        particles[0][3] = new Particle(WIDTH / 2.0, HEIGHT / 2.0 + d * R0 - shift, 0, 0);
        particles[0][4] = new Particle(WIDTH / 2.0, HEIGHT / 2.0  - d * R0 - shift, 0, 0);

        return particles;
    }
}
