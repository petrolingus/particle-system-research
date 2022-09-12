package me.petrolingus.unn.test;

import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;

import java.util.List;

public class Controller {

    public StackedAreaChart<Number, Number> interactionEnergyChart;

    public TextField epsilonField;
    public TextField sigmaField;
    public TextField aField;
    public TextField bField;
    public TextField hopField;

    public void initialize() {

        onButton();
    }

    public void onButton() {
        drawInteractionEnergyChart();
    }

    private void drawInteractionEnergyChart() {
        double a = Double.parseDouble(aField.getText());
        double b = Double.parseDouble(bField.getText());
        double h = Double.parseDouble(hopField.getText());
        int n = (int) Math.floor((b - a) / h);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("U");
        for (int i = 0; i < n; i++) {
            double x = a + i * h;
            double y = U(x);
            series.getData().add(new XYChart.Data<>(x, y));
        }
        if (!interactionEnergyChart.getData().isEmpty()) {
            interactionEnergyChart.getData().clear();
        }
        interactionEnergyChart.getData().add(series);
    }

    private double U(double r) {
        double epsilon = Double.parseDouble(epsilonField.getText());
        double sigma = Double.parseDouble(sigmaField.getText());
        double r0 = sigma;
        double r1 = 1.1 * r0;
        double r2 = 1.8 * r0;
        double K = 0;
        if (r < r1) {
            K = 1;
        } else if (r < r2) {
            K = Math.pow(1.0 - Math.pow((r - r1)/(r1 - r2), 2), 2);
        }
        sigma = r0 / Math.pow(2, 1.0/6.0);
        double sr = Math.pow(sigma / r, 6);
        return 4.0 * epsilon * (sr * sr - sr) * K;
    }

    private double F(double r) {
        double epsilon = Double.parseDouble(epsilonField.getText());
        double sigma = Double.parseDouble(sigmaField.getText());
        double a6 = Math.pow(sigma, 6);
        return -12.0 * epsilon * a6 * (a6 / Math.pow(r, 6) - 1) * (r / Math.pow(r, 8));
    }
}
