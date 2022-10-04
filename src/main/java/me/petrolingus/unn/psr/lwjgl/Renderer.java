package me.petrolingus.unn.psr.lwjgl;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.algorithm.Algorithm;
import me.petrolingus.unn.psr.core.model.Particle;
import me.petrolingus.unn.psr.lwjgl.mesh.Mesh;
import me.petrolingus.unn.psr.lwjgl.shader.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final int width;

    private final int height;

    private long window;

    private ByteBuffer buffer;

    private boolean isRunning;

    public Renderer(int width, int height, long window) {
        this.width = width;
        this.height = height;
        this.window = window;
        this.buffer = BufferUtils.createByteBuffer(width * height * 4);
        System.out.println("Buffer Size:" + buffer);
    }

    public void run() {
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.82f, 0.87f, 0.89f, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        isRunning = true;

        loop();
    }

    public void stop() {
        isRunning = false;
    }

    public ByteBuffer getBuffer() {
        return buffer.asReadOnlyBuffer();
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void loop() {

        ShaderProgram shaderProgram = null;
        try {
            String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
            String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
            shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
            shaderProgram.createUniform("width");
            shaderProgram.createUniform("height");
            shaderProgram.createUniform("scale");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        float[] vertices = {
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
        };
        int[] indices = {
                0, 1, 3,
                1, 2, 3
        };
        Mesh mesh = new Mesh(vertices, indices);

        // Create FloatBuffer
        float[] positions = new float[3 * Configuration.N];
        int positionsLength = positions.length;
        for (int i = 0; i < positionsLength; i++) {
            positions[i] = (float) (ThreadLocalRandom.current().nextDouble());
            positions[i] = 0.5f;
        }
        FloatBuffer positionsFloatBuffer = MemoryUtil.memAllocFloat(positionsLength);
        positionsFloatBuffer.put(positions).flip();
        mesh.bufferDataUpdate(positionsFloatBuffer);

        double FPS_TARGET = 75;
        double FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;
        long frameStart = System.currentTimeMillis();

        while (isRunning) {

            long frameStop = System.currentTimeMillis();

            if (frameStop - frameStart > FRAME_TIME_TARGET) {
                glClear(GL_COLOR_BUFFER_BIT);

                List<Particle> particles = Algorithm.getParticles();
                for (int i = 0; i < particles.size(); i++) {
                    positions[3 * i] = (float) (particles.get(i).x / Configuration.WIDTH);
                    positions[3 * i + 1] = (float) (particles.get(i).y / Configuration.WIDTH);
                }

                positionsFloatBuffer.put(positions).flip();
                mesh.bufferDataUpdate(positionsFloatBuffer);

                shaderProgram.bind();
                shaderProgram.setUniform("width", width);
                shaderProgram.setUniform("height", height);
                shaderProgram.setUniform("scale", (float) (Configuration.R0 / Configuration.WIDTH));
                mesh.drawInstances(Configuration.N);
                shaderProgram.unbind();

                glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

                frameStart = frameStop;
            }
        }
    }

}
