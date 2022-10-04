package me.petrolingus.unn.psr.controller;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.scene.layout.StackPane;
import me.petrolingus.unn.psr.core.algorithm.Algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChartController {

    private final Map<ChartTypes, XYChart> charts = new HashMap<>();

    private ScheduledExecutorService executor;

    public void createChart(ChartTypes chartType, StackPane pane) {
        charts.put(chartType, createChartOnPane(pane));
    }

    public void initializeTemperatureChart() {
        XYChart temperatureChart = charts.get(ChartTypes.TEMPERATURE_CHART);
        final DoubleDataSet temperatureDataSet = new DoubleDataSet("Temperature");
        temperatureChart.getDatasets().setAll(temperatureDataSet);

        XYChart energyChart = charts.get(ChartTypes.ENERGY_CHART);
        final DoubleDataSet kineticEnergyDataSet = new DoubleDataSet("Kinetic Energy");
        final DoubleDataSet potentialEnergyDataSet = new DoubleDataSet("Potential Energy");
        final DoubleDataSet fullEnergyDataSet = new DoubleDataSet("Full Energy");
        energyChart.getDatasets().setAll(kineticEnergyDataSet, potentialEnergyDataSet, fullEnergyDataSet);

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            double x = Algorithm.getStep();
//            if (x > 0) {
                double temperature = Algorithm.getCurrentTemperature();
                temperatureDataSet.add(x, temperature);
//
                double kinetic = Algorithm.getCurrentKinetic();
                kineticEnergyDataSet.add(x, kinetic);

                double potential = Algorithm.getCurrentPotential();
                potentialEnergyDataSet.add(x, potential);

                double full = Algorithm.getCurrentFull();
                fullEnergyDataSet.add(x, full);
//            }
        }, 0, 32, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
    }

    private XYChart createChartOnPane(StackPane pane) {

        XYChart chart = new XYChart(new DefaultNumericAxis(), new DefaultNumericAxis());
        chart.setAnimated(false);
        pane.getChildren().add(chart);

        final ErrorDataSetRenderer errorRenderer = new ErrorDataSetRenderer();
        chart.getRenderers().setAll(errorRenderer);
        errorRenderer.setErrorType(ErrorStyle.NONE);
        errorRenderer.setDrawMarker(false);

        return chart;
    }

    public enum ChartTypes {
        ENERGY_CHART,
        TEMPERATURE_CHART,
        SELF_DIFFUSION_CHART
    }
}
