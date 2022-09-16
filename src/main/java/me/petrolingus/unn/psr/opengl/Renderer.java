package me.petrolingus.unn.psr.opengl;

import me.petrolingus.unn.psr.core.Algorithm;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.ParticleData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class Renderer {

    private final int width;

    private final int height;

    private final long window;

    private ByteBuffer buffer;

    private Algorithm algorithm;

    private boolean running = true;

    public Renderer(int width, int height, long window) {
        this.width = width;
        this.height = height;
        this.window = window;
        initialize();
    }

    private void initialize() {

        algorithm = new Algorithm();
        algorithm.start();

//        float pointSize = (float) (width * Configuration.particleSize / Configuration.WIDTH);

        GL.createCapabilities();
//        GL11.glViewport(0, 0, 720, 720);
        GL11.glDisable (GL11.GL_POINT_SMOOTH);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glPointSize(400);
        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    void loop() {

        double FPS_TARGET = 120;
        double FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;

        RuntimeConfiguration.running = true;

        long frameStart = System.currentTimeMillis();

        while (running) {
            long frameStop = System.currentTimeMillis();
            if (frameStop - frameStart > FRAME_TIME_TARGET) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                try {
                    int currentFrame = RuntimeConfiguration.currentFrame;
                    for (ParticleData particle : algorithm.getParticleData(currentFrame)) {
                        double x = 1 - particle.x() / (0.5 * Configuration.WIDTH);
                        double y = 1 - particle.y() / (0.5 * Configuration.HEIGHT);
                        int n = 64;
                        double h = 2.0 * Math.PI / (n - 1);
                        double pointSize = 2.0 * Configuration.particleRadius / Configuration.WIDTH;
                        GL11.glBegin(GL11.GL_POLYGON);
                        for (int i = 0; i < n; i++) {
                            double phi = i  * h;
                            double xx = pointSize * Math.cos(phi);
                            double yy = pointSize * Math.sin(phi);
                            GL11.glVertex2d(xx + x, yy + y);
                        }
                        GL11.glEnd();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                frameStart = frameStop;
            }
        }
    }

    void kill() {
        running = false;
    }

    ByteBuffer getBuffer() {
        return buffer.asReadOnlyBuffer();
    }
}
