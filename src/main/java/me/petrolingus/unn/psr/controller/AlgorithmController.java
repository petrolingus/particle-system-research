package me.petrolingus.unn.psr.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.DefaultAlgorithm;
import me.petrolingus.unn.psr.core.OldAlgorithm;
import me.petrolingus.unn.psr.core.generator.ParticleGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlgorithmController {

    private Label stepsLabel;

    private boolean isRunning = false;

    private OldAlgorithm algorithm;

    public AlgorithmController() {
        Configuration.recalculate();
    }

    public void run() {
        ParticleGenerator particleGenerator = new ParticleGenerator();
        this.algorithm = new OldAlgorithm(particleGenerator);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            for (int i = 0; i < 100; i++) {
                algorithm.next();
            }
            algorithm.snapshot();
        }, 0, 1, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(() -> {
            Platform.runLater(() -> {
                stepsLabel.setText("Steps: " + DefaultAlgorithm.getStep());
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        algorithm = null;
        DefaultAlgorithm.stop();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setStepsLabel(Label stepsLabel) {
        this.stepsLabel = stepsLabel;
    }
}
