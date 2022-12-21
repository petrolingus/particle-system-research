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

        AtomicLong start = new AtomicLong(System.nanoTime());

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            double x = Algorithm.getStep();

            long s = System.nanoTime();

            double kinetic = Algorithm.getCurrentKinetic();
            kineticEnergyDataSet.add(x, kinetic);

            double potential = Algorithm.getCurrentPotential();
            potentialEnergyDataSet.add(x, potential);

            double full = Algorithm.getCurrentFull();
            fullEnergyDataSet.add(x, full);

//            int size = Algorithm.temperatureList.size();
//            int n = size - temperatureDataSet.getDataCount();
//            double[] xis = new double[n];
//            double[] yis = new double[n];
//            for (int i = temperatureDataSet.getDataCount(); i < size; i++) {
//                xis[i] = i;
//                yis[i] = Algorithm.temperatureList.get(i);
//            }
//            temperatureDataSet.set(xis, yis);

//            System.out.println(Algorithm.temperatureList.get(Algorithm.temperatureList.size() - 1));

            double temperature = Algorithm.getCurrentTemperature();
            temperatureDataSet.add(x, temperature);

            if (selfDataSet.getDataCount() > 0) {
                selfDataSet.clearData();
            }
            for (int i = 0; i < Algorithm.rList.size(); i++) {
                selfDataSet.add(i, Algorithm.rList.get(i));
            }


//            long e = System.nanoTime();

//            long stop = System.nanoTime();
//            long diff = stop - start.get();

//            if (diff > 1_000_000_000) {
//                System.out.println("Diff: " + ((e - s) / 1_000_000.0));
//                start.set(stop);
//            }
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
