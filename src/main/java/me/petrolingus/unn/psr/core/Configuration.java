package me.petrolingus.unn.psr.core;

public interface Configuration {

    int Lx = 720;
    int Ly = 720;
    int particleCount = 100;
    double particleSize = Lx / 130.0; // diameter
    double maxSpeed = 10;
    double dt = 0.02;
    int countOfSteps = 100_000;

    double D = particleSize * 1e-8;
    double a6 = Math.pow(particleSize, 6);
    double dt2 = dt * dt;
    double c = 12 * D * a6;

}
