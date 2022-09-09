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

        float pointSize = (float) (width * Configuration.particleSize / Configuration.Lx);

        GL.createCapabilities();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glPointSize(pointSize);
        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    void loop() throws Exception {

        double FPS_TARGET = 120;
        double FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;

        int currentFrame = 0;

        long frameStart = System.currentTimeMillis();

        while (running) {

            long frameStop = System.currentTimeMillis();
            if (frameStop - frameStart > FRAME_TIME_TARGET) {

                // Render
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

                // Drawing all particles
                GL11.glBegin(GL11.GL_POINTS);
                for (ParticleData particle : algorithm.getParticleData(currentFrame)) {
                    double x = 1 - particle.x() / (0.5 * Configuration.Lx);
                    double y = 1 - particle.y() / (0.5 * Configuration.Ly);
                    GL11.glVertex2d(x, y);
                }
                GL11.glEnd();

                currentFrame++;
                currentFrame = currentFrame % Configuration.countOfSteps;

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
