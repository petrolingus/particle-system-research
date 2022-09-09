package me.petrolingus.unn.psr;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import me.petrolingus.unn.psr.core.Algorithm;
import me.petrolingus.unn.psr.opengl.Window;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Controller {

    public Canvas canvas;

    public Label iterationLabel;

    public LineChart<Number, Number> chart;

    public void initialize() {

        final int width = (int) canvas.getWidth();
        final int height = (int) canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);

        Window window = new Window(width, height);
        new Thread(window::run).start();

        WritableImage img = new WritableImage(width, height);

        new Thread(() -> {

            PixelWriter pw = img.getPixelWriter();
            int bpp = 4;
            int[] pixels = new int[width * height];

            while (true) {
                if (!window.isInitialize()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                ByteBuffer buffer = window.getBuffer();
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        int i = (x + (width * y)) * bpp;
                        int r = buffer.get(i) & 0xFF;
                        int g = buffer.get(i + 1) & 0xFF;
                        int b = buffer.get(i + 2) & 0xFF;
                        pixels[(height - (y + 1)) * width + x] = (0xFF << 24) | (r << 16) | (g << 8) | b;
                    }
                }
                pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
            }
        }).start();

        new Thread(() -> {
            long start = System.nanoTime();
            while (true) {

                if (!window.isInitialize()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                long stop = System.nanoTime();
                if (stop - start > 16_000_000) {
                    start = stop;
                    context.drawImage(img, 0, 0);
                }
            }
        }).start();

        new Thread(() -> {

            while (!Algorithm.isDone()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Platform.runLater(() -> {
                List<Double> averageKineticEnergyList = Algorithm.getAverageKineticEnergyList();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                int maxPoints = 1000;
                int step = averageKineticEnergyList.size() / maxPoints;
                for (int i = 0; i < maxPoints; i += step) {
                    int currentFrame = i * step;
                    series.getData().add(new XYChart.Data<>(currentFrame, averageKineticEnergyList.get(currentFrame)));
                }
                chart.getData().add(series);
            });

        }).start();

    }
}
