package me.petrolingus.unn.psr.controller;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import me.petrolingus.unn.psr.core.Configuration;

public class UiController {

    public StackPane canvasPane;

    public StackPane energyChartPane;
    public StackPane temperatureChartPane;
    public StackPane selfDiffusionChartPane;

    public Label stepsLabel;

    private CanvasController canvasController;
    private ChartController chartController;
    private AlgorithmController algorithmController;

    public void initialize() {

        canvasController = new CanvasController(canvasPane);

        chartController = new ChartController();
        chartController.createChart(ChartController.ChartTypes.ENERGY_CHART, energyChartPane);
        chartController.createChart(ChartController.ChartTypes.TEMPERATURE_CHART, temperatureChartPane);
        chartController.createChart(ChartController.ChartTypes.SELF_DIFFUSION_CHART, selfDiffusionChartPane);

        algorithmController = new AlgorithmController();
        algorithmController.setStepsLabel(stepsLabel);
    }

    public void onStartButton() {
        canvasController.run();
        algorithmController.run();
        chartController.initializeTemperatureChart();
    }

    public void onStopButton() {
        chartController.stop();
        algorithmController.stop();
        canvasController.stop();
    }

}
