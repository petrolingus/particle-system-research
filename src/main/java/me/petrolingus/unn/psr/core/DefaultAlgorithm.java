package me.petrolingus.unn.psr.core;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultAlgorithm {

    static boolean done = false;

    static List<Double> temperatureList = new ArrayList<>();
    static List<Double> potentialEnergyList = new ArrayList<>();
    static List<Double> kineticEnergyList = new ArrayList<>();
    static List<Double> fullEnergyList = new ArrayList<>();
    static List<Particle> particleList = new ArrayList<>();

    static int step = 0;
    static double potential = 0;
    static double kinetic = 0;
    static double full = 0;
    static double temperature = 0;

    public static List<Double> getTemperatureList() {
        return temperatureList;
    }

    public static List<Double> getPotentialEnergyList() {
        return potentialEnergyList;
    }

    public static List<Double> getKineticEnergyList() {
        return kineticEnergyList;
    }

    public static List<Double> getFullEnergyList() {
        return fullEnergyList;
    }

    public static List<Particle> getParticles() {
        return particleList;
    }

    public static void stop() {
        step = 0;
    }

    public abstract void start();

    public abstract List<Particle> getParticleData(int index);

    public static boolean isDone() {
        return done;
    }

    public static int getStep() {
        return step;
    }

    public static double getCurrentTemperature() {
        return temperature;
    }

    public static double getCurrentPotential() {
        return potential;
    }

    public static double getCurrentKinetic() {
        return kinetic;
    }


}
