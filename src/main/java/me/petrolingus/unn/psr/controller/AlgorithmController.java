package me.petrolingus.unn.psr.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.algorithm.Algorithm;
import me.petrolingus.unn.psr.core.algorithm.DefaultAlgorithm;
import me.petrolingus.unn.psr.core.generator.ParticleGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlgorithmController {

    private Label stepsLabel;

    private DefaultAlgorithm algorithm;

    public AlgorithmController() {
        Configuration.recalculate();
    }

    public void run() {
        ParticleGenerator particleGenerator = new ParticleGenerator();
        this.algorithm = new DefaultAlgorithm(particleGenerator);
//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleWithFixedDelay(() -> {
            for (int i = 0; i < Configuration.NSNAP; i++) {
                algorithm.next();
            }
            algorithm.snapshot();
        }, 0, 1, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(() -> {
            Platform.runLater(() -> {
                stepsLabel.setText("Steps: " + Algorithm.getStep());
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        algorithm = null;
    }

    public void setStepsLabel(Label stepsLabel) {
        this.stepsLabel = stepsLabel;
    }
}
