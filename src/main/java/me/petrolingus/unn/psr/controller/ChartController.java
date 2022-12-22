package me.petrolingus.unn.psr.controller;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.scene.layout.StackPane;
import jdk.jshell.execution.JdiInitiator;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.Timer;
import me.petrolingus.unn.psr.core.algorithm.Algorithm;
import me.petrolingus.unn.psr.core.algorithm.ShiftData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ChartController {

    private final Map<ChartTypes, XYChart> charts = new HashMap<>();

    private ScheduledExecutorService executor;

    public void createChart(ChartTypes chartType, StackPane pane) {
        charts.put(chartType, createChartOnPane(pane));
    }

    public void initializeTemperatureChart() {
        XYChart temperatureChart = charts.get(ChartTypes.TEMPERATURE_CHART);
        final DoubleDataSet temperatureDataSet = new DoubleDataSet("Temperature");
        temperatureDataSet.setStyle("strokeWidth=1");
        temperatureChart.getDatasets().clear();
        temperatureChart.getDatasets().setAll(temperatureDataSet);

        XYChart energyChart = charts.get(ChartTypes.ENERGY_CHART);
        final DoubleDataSet kineticEnergyDataSet = new DoubleDataSet("Kinetic Energy");
        final DoubleDataSet potentialEnergyDataSet = new DoubleDataSet("Potential Energy");
        final DoubleDataSet fullEnergyDataSet = new DoubleDataSet("Full Energy");
        energyChart.getDatasets().setAll(kineticEnergyDataSet, potentialEnergyDataSet, fullEnergyDataSet);

        XYChart selfChart = charts.get(ChartTypes.SELF_DIFFUSION_CHART);
        final DoubleDataSet selfDataSet = new DoubleDataSet("Self");
        selfDataSet.setStyle("strokeWidth=1");
        selfChart.getDatasets().clear();
        selfChart.getDatasets().setAll(selfDataSet);

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {

            double x = Algorithm.getStep();

            double kinetic = Algorithm.getCurrentKinetic();
            kineticEnergyDataSet.add(x, kinetic);

            double potential = Algorithm.getCurrentPotential();
            potentialEnergyDataSet.add(x, potential);

            double full = Algorithm.getCurrentFull();
            fullEnergyDataSet.add(x, full);

            double temperature = Algorithm.getCurrentTemperature();
            temperatureDataSet.add(x, temperature);

//            if (selfDataSet.getDataCount() > 0) {
//                selfDataSet.clearData();
//            }
            while (true) {
                ShiftData shiftData = Algorithm.rList.poll();
                if (shiftData == null) {
                    break;
                } else {
                    selfDataSet.add(shiftData.getT(), shiftData.getValue());
                }
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
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
