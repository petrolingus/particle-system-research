package me.petrolingus.unn.psr.core.algorithm;

import me.petrolingus.unn.psr.core.model.Particle;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {

    static List<Particle> particleList = new ArrayList<>();

    static int step = 0;

    static double kinetic = 0;
    static double potential = 0;
    static double full = 0;
    static double temperature = 0;

    public static List<Double> temperatureList = new ArrayList<>();

    public static List<Double> rList = new ArrayList<>();

    public static List<Particle> getParticles() {
        return particleList;
    }

    public static int getStep() {
        return step;
    }

    public static double getCurrentTemperature() {
        return temperature;
    }

    public static double getCurrentKinetic() {
        return kinetic;
    }

    public static double getCurrentPotential() {
        return potential;
    }

    public static double getCurrentFull() {
        return full;
    }

    public static void stop() {
        step = 0;
    }
}
