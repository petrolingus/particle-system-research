package me.petrolingus.unn.psr;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import me.petrolingus.unn.psr.core.Algorithm;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.ui.FrameController;
import me.petrolingus.unn.psr.core.ui.UiConfiguration;
import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;
import me.petrolingus.unn.psr.opengl.Window;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {

    public TextField nField;
    public TextField maxSpeedField;
    public TextField sigmaField;
    public TextField epsilonField;
    public TextField tauField;
    public TextField stepsField;

    public Canvas canvas;

    public Button frameButton;
    public ChoiceBox<String> frameRate;
    public Label frameLabel;
    public Slider frameSlider;

    public LineChart<Number, Number> chart;

    public Button calculateSimulationButton;
    public Button clearSimulationButton;

    private static volatile Window window;
    private static WritableImage img;
    private final List<Thread> thread = new ArrayList<>();
    private ExecutorService executorService;

    public void setConfig() {
//        Configuration.N = Integer.parseInt(nField.getText());
//        Configuration.MAX_SPEED = Double.parseDouble(maxSpeedField.getText());
//        Configuration.SIGMA = Double.parseDouble(sigmaField.getText());
//        Configuration.EPSILON = Double.parseDouble(epsilonField.getText());
//        Configuration.TAU = Double.parseDouble(tauField.getText());
//        Configuration.STEPS = Integer.parseInt(stepsField.getText());
        Configuration.recalculate();
    }

    public void initialize() {

        setConfig();

        clearSimulationButton.disableProperty().bind(calculateSimulationButton.disableProperty().not());

        ObservableList<String> frameRates = FXCollections.observableArrayList(
                "120fps",
                "240fps",
                "480fps",
                "960fps",
                "1920fps"
        );
        frameRate.getItems().addAll(frameRates.stream().toList());
        frameRate.setValue(frameRates.get(0));
        frameRate.valueProperty().addListener((newValue) -> {
            String name = ((ObjectProperty<String>) newValue).getValue().replaceAll("\\D+", "");
            System.out.println(Integer.parseInt(name));
            UiConfiguration.frameRate = Integer.parseInt(name);
        });

        final int width = (int) canvas.getWidth();
        final int height = (int) canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);

        FrameController frameController = new FrameController();
        Thread frameControllerThread = new Thread(frameController);
        frameControllerThread.setName("Frame Controller");
        frameControllerThread.start();

        frameSlider.valueProperty().addListener((value) -> {
            frameSlider.setMax(RuntimeConfiguration.maxFrame);
            frameSlider.setMin(0);
            RuntimeConfiguration.currentFrame = (int) ((DoubleProperty) value).getValue().doubleValue();
        });

        Image playImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("play.png")));
        Image stopImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("pause.png")));
        frameButton.setOnAction(e -> {
            Button button = (Button) e.getSource();
            UiConfiguration.autoplay = !UiConfiguration.autoplay;
            if (UiConfiguration.autoplay) {
                button.setGraphic(new ImageView(stopImage));
            } else {
                button.setGraphic(new ImageView(playImage));
            }
        });

        onCleanButton();
    }

    public void onCalculateButton() {
        calculateSimulationButton.setDisable(true);
        setConfig();
        thread.forEach(executorService::submit);
        onFrameButton();
    }

    public void onCleanButton() {
        calculateSimulationButton.setDisable(false);
        RuntimeConfiguration.running = false;
        RuntimeConfiguration.currentFrame = 0;
        if (window != null) {
            window.kill();
            executorService.shutdown();
            thread.clear();
        }
        createThreads();
        executorService = Executors.newFixedThreadPool(4);
    }

    public void createThreads() {

        final int width = (int) canvas.getWidth();
        final int height = (int) canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();

        window = new Window(width, height);
        Thread thread0 = new Thread(window::run);

        img = new WritableImage(width, height);

        Thread thread1 = new Thread(() -> {
            PixelWriter pw = img.getPixelWriter();
            int bpp = 4;
            int[] pixels = new int[width * height];
            while (!window.isKilled()) {
                if (!window.isInitialize()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                ByteBuffer buffer = window.getBuffer();
                if (window.isKilled()) {
                    break;
                }
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
        });

        Thread thread2 = new Thread(() -> {
            long start = System.nanoTime();
            while (!window.isKilled()) {
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
                    Platform.runLater(() -> {
                        context.drawImage(img, 0, 0);
                        frameLabel.setText(RuntimeConfiguration.currentFrame + " / " + RuntimeConfiguration.maxFrame);
                        if (UiConfiguration.autoplay) {
                            frameSlider.setMax(RuntimeConfiguration.maxFrame);
                            frameSlider.setMin(0);
                            frameSlider.setValue(RuntimeConfiguration.currentFrame);
                        }
                    });
                }
            }
        });

        Thread thread3 = new Thread(() -> {
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
                if (chart.getData() != null) {
                    chart.getData().clear();
                }
                chart.getData().add(series);
            });
        });

        thread.add(thread0);
        thread.add(thread1);
        thread.add(thread2);
        thread.add(thread3);
    }

    public void onFrameButton() {

    }

}
