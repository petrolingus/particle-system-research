package me.petrolingus.unn.test;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.petrolingus.unn.psr.core.Configuration;

import java.util.List;

public class Controller {

    public Pane pane;
    public Canvas canvas;

    public StackedAreaChart<Number, Number> interactionEnergyChart;
    public StackedAreaChart<Number, Number> kinetic;

    public TextField epsilonField;
    public TextField sigmaField;
    public TextField aField;
    public TextField bField;
    public TextField hopField;

    public void initialize() {
        canvas = new ResizableCanvas();
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(canvas);


//        onButton();
    }

    public void onButton() {
        drawInteractionEnergyChart();
    }

    public double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    private void drawInteractionEnergyChart() {

        new Thread(() -> {

            Algorithm algorithm = new Algorithm();
            List<List<Particle>> run = algorithm.run();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Kinetic Energy");
            for (int i = 0; i < run.size(); i++) {
                double y = 0;
                for (Particle p : run.get(i)) {
                    y += p.getKE();
                }
                series.getData().add(new XYChart.Data<>(i, y));
            }
            Platform.runLater(() -> {
                if (kinetic.getData().size() != 0) {
                    kinetic.getData().clear();
                }
                kinetic.getData().add(series);
            });

            double width = pane.getWidth();
            double height = pane.getHeight();

            GraphicsContext context = canvas.getGraphicsContext2D();

            System.out.println("SIZE: " + run.size());

            int it = 0;
            while (true) {
                List<Particle> particles = run.get(it);

                context.setFill(Color.BLACK);
                context.fillRect(0, 0, width, height);
                context.setFill(Color.WHITE);
                for (Particle p : particles) {
                    double x = width * normalize(p.x, 0, 30);
                    double y = height * normalize(p.y, 0, 30);
                    double r = width * normalize(Algorithm.SIGMA, 0, Algorithm.WIDTH);
                    r = 4;
                    context.fillOval(x - r, y - r, r * 2, r * 2);
                }

                it++;
                if (it == run.size()) {
                    it = 0;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();

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
            K = Math.pow(1.0 - Math.pow((r - r1) / (r1 - r2), 2), 2);
        }
        sigma = r0 / Math.pow(2, 1.0 / 6.0);
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
