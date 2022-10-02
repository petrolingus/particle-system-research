package me.petrolingus.unn.psr.controller;

import de.gsi.chart.ui.ResizableCanvas;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import me.petrolingus.unn.psr.lwjgl.Window;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CanvasController {

    public ResizableCanvas canvas;

    public volatile Window window;

    public CanvasController(StackPane canvasPane) {

        canvas = new ResizableCanvas();
        canvasPane.getChildren().add(canvas);

        canvasPane.widthProperty().addListener((width) -> {
            Double w = ((ReadOnlyDoubleProperty) width).getValue();
            canvas.resize(w, canvas.getHeight());
            draw();
        });

        canvasPane.heightProperty().addListener((height) -> {
            Double h = ((ReadOnlyDoubleProperty) height).getValue();
            canvas.resize(canvas.getWidth(), h);
            draw();
        });

    }

    public void run() {

        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        window = new Window(width, height);

        final WritableImage img = new WritableImage(width, height);
        final PixelWriter pw = img.getPixelWriter();
        final int bpp = 4;
        final int[] pixels = new int[width * height];
        System.out.println("Pixels Size:" + pixels.length);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {

            try {
                if (window != null && window.isRunning()) {
                    ByteBuffer buffer = window.getBuffer();
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int i = ((height - y - 1) * width + x) * bpp;
                            int r = buffer.get(i) & 0xFF;
                            int g = buffer.get(i + 1) & 0xFF;
                            int b = buffer.get(i + 2) & 0xFF;
                            pixels[y * width + x] = (0xFF << 24) | (r << 16) | (g << 8) | b;
                        }
                    }
                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
                    Platform.runLater(() -> {
                        GraphicsContext context = canvas.getGraphicsContext2D();
                        context.drawImage(img, 0, 0);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 16, TimeUnit.MILLISECONDS);

        new Thread(() -> {
            System.out.println("FOO:" + window);
            window.run();
            executor.shutdown();
            System.out.println("WINDOW CLOSED");
        }).start();
    }

    public void stop() {
        window.stop();
        window = null;
    }

    private void draw() {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.BLACK);
        graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
