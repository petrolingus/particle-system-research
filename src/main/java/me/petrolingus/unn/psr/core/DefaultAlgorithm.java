package me.petrolingus.unn.psr.core;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultAlgorithm {

    static boolean done = false;

    static List<Double> temperatureList = new ArrayList<>();
    static List<Double> potentialEnergyList = new ArrayList<>();
    static List<Double> kineticEnergyList = new ArrayList<>();
    static List<Double> fullEnergyList = new ArrayList<>();

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

    public abstract void start();

    public abstract List<Particle> getParticleData(int index);

    public static boolean isDone() {
        return done;
    }
}
