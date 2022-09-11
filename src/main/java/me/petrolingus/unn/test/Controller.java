package me.petrolingus.unn.test;

import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;

import java.util.List;

public class Controller {

    public StackedAreaChart<Number, Number> positionChart;
    public StackedAreaChart<Number, Number> chart2;

    public TextField epsilonField;
    public TextField sigmaField;
    public TextField aField;
    public TextField bField;
    public TextField hopField;

    public void initialize() {
        Algorithm algorithm = new Algorithm();
        List<List<Particle>> snapshots = algorithm.run();

        if (!positionChart.getData().isEmpty()) {
            positionChart.getData().clear();
        }

        for (int i = 0; i < snapshots.size(); i++) {
            XYChart.Series<Number, Number> positions = new XYChart.Series<>();
            for (int j = 0; j < snapshots.get(0).size(); j++) {
                double x = snapshots.get(i).get(j).x;
                double y = snapshots.get(i).get(j).y;
                positions.getData().add(new XYChart.Data<>(x, y));
            }
            positionChart.getData().add(positions);
        }

    }

    public void onButton() {

        double a = Double.parseDouble(aField.getText());
        double b = Double.parseDouble(bField.getText());
        double h = Double.parseDouble(hopField.getText());
        int n = (int) Math.floor((b - a) / h);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < n; i++) {
            double x = a + i * h;
            double y = U(x);
            series.getData().add(new XYChart.Data<>(x, y));
        }
        if (!chart2.getData().isEmpty()) {
            chart2.getData().clear();
        }
        chart2.getData().add(series);

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        for (int i = 0; i < n; i++) {
            double x = a + i * h;
            double y = -F(x);
            series2.getData().add(new XYChart.Data<>(x, y));
        }
//        if (!chart2.getData().isEmpty()) {
//            chart2.getData().clear();
//        }
        chart2.getData().add(series2);

    }

    private double U(double r) {
        double epsilon = Double.parseDouble(epsilonField.getText());
        double sigma = Double.parseDouble(sigmaField.getText());
        double sr = Math.pow(sigma / r, 6);
        return 4.0 * epsilon * (sr * sr - sr);
    }

    private double F(double r) {
        double epsilon = Double.parseDouble(epsilonField.getText());
        double sigma = Double.parseDouble(sigmaField.getText());
        double a6 = Math.pow(sigma, 6);
        return -12.0 * epsilon * a6 * (a6 / Math.pow(r, 6) - 1) * (r / Math.pow(r, 8));
    }
}
