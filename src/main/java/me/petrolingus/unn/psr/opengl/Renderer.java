package me.petrolingus.unn.psr.opengl;

import me.petrolingus.unn.psr.core.Algorithm;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.Particle;
import me.petrolingus.unn.psr.core.ParticleData;
import me.petrolingus.unn.psr.core.generator.ParticleGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

        ParticleGenerator particleGenerator = new ParticleGenerator();
        algorithm = new Algorithm(particleGenerator);
        algorithm.start();

        GL.createCapabilities();
        GL11.glClearColor(0.82f, 0.87f, 0.89f, 0.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    void loop() throws Exception {

        String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
        String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
        ShaderProgram shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("size");
        shaderProgram.createUniform("scale");

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

        double FPS_TARGET = 120;
        double FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;

        RuntimeConfiguration.running = true;

        long frameStart = System.currentTimeMillis();

        while (running) {
            long frameStop = System.currentTimeMillis();
            if (frameStop - frameStart > FRAME_TIME_TARGET) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                Particle[] particleData = algorithm.getParticleData(RuntimeConfiguration.currentFrame);
                for (int i = 0; i < particleData.length; i++) {
                    positions[3 * i] = (float) (particleData[i].x / Configuration.WIDTH);
                    positions[3 * i + 1] = (float) (particleData[i].y / Configuration.WIDTH);
                }
                positionsFloatBuffer.put(positions).flip();
                mesh.bufferDataUpdate(positionsFloatBuffer);

                shaderProgram.bind();
                shaderProgram.setUniform("size", 704);
                shaderProgram.setUniform("scale", (float) (Configuration.R0 / Configuration.WIDTH));
                mesh.drawInstances(Configuration.N);
                shaderProgram.unbind();

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
