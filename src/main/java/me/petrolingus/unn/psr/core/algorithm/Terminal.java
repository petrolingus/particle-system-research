package me.petrolingus.unn.psr.core.algorithm;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.generator.ParticleGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Terminal {

    public static void main(String[] args) throws IOException {

        Configuration.recalculate();

        ParticleGenerator particleGenerator = new ParticleGenerator();
        DefaultAlgorithm algorithm = new DefaultAlgorithm(particleGenerator);

        StringBuilder data = new StringBuilder();

        while (true) {
            for (int i = 0; i < Configuration.NSNAP; i++) {
                algorithm.next();
            }
            algorithm.snapshot();

            double step = Algorithm.getStep();
            double kinetic = Algorithm.getCurrentKinetic();
            double potential = Algorithm.getCurrentPotential();
            double full = Algorithm.getCurrentFull();

            data
                    .append(getDoubleCsv(step)).append(',')
                    .append(getDoubleCsv(kinetic)).append(',')
                    .append(getDoubleCsv(potential)).append(',')
                    .append(getDoubleCsv(full)).append(',')
                    .append("\n");

            if (step == 100_000) {
                break;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("data.csv"));
        writer.write(data.toString());
        writer.close();
    }

    private static String getDoubleCsv(double value) {
        return '"' + Double.toString(value).replace('.', ',') + '"';
    }
}
