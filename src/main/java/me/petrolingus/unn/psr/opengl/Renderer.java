package me.petrolingus.unn.psr.opengl;

import me.petrolingus.unn.psr.core.Algorithm;
import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.core.ParticleData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

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
        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glPointSize(10);
        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    void loop() throws Exception {

        List<ParticleData> data = algorithm.getParticleData(0);
//        float[] positions = new float[3 * data.size()];
//        int[] indices = new int[data.size()];
//        for (int i = 0; i < data.size(); i++) {
////            positions[3 * i] = (float) (2 * data.get(i).x() / Configuration.Lx - 1);
////            positions[3 * i + 1] = (float) (2 * data.get(i).y() / Configuration.Ly - 1);
//            positions[3 * i] = (float) data.get(i).x();
//            positions[3 * i + 1] = (float) data.get(i).y();
//            positions[3 * i + 2] = 0;
//            indices[i] = i;
//        }

        float[] positions = {
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f
        };

        float[] textures = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        Mesh mesh = new Mesh(positions, textures, indices);
//        mesh.createTexture();

        String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
        String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
        ShaderProgram shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
//        shaderProgram.createUniform("width");
//        shaderProgram.createUniform("particleX");
//        shaderProgram.createUniform("particleY");

        double FPS_TARGET = 120;
        double FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;

        RuntimeConfiguration.running = true;

        long frameStart = System.currentTimeMillis();

        float timer = 0;

        while (!glfwWindowShouldClose(window)) {

            long frameStop = System.currentTimeMillis();
            if (frameStop - frameStart > FRAME_TIME_TARGET) {

                Matrix4f projectionMatrix = new Matrix4f().setOrtho(-1f, 1f, -1f, 1f, 0.01f, 1000.f);
//                projectionMatrix.scale(1f);
                Vector3f cameraPos = new Vector3f(0.0f, 0.0f, -1.0f);
                Vector3f zeroVector = new Vector3f(0, 0, 0);
                Vector3f upVector = new Vector3f(0, 1, 0);
                Matrix4f viewMatrix = new Matrix4f().setLookAt(cameraPos, zeroVector, upVector);

                // Render
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

                // Drawing all particles
//                try {
//                    shaderProgram.bind();
//                    shaderProgram.setUniform("projectionMatrix", projectionMatrix);
//                    shaderProgram.setUniform("viewMatrix", viewMatrix);
//                    shaderProgram.setUniform("width", (float) width);
//                    GL11.glBegin(GL11.GL_POINTS);
//                    int currentFrame = RuntimeConfiguration.currentFrame;
//                    if (currentFrame < 0 || currentFrame > Configuration.countOfSteps) {
//                        System.err.println("WTF!");
//                    }
//                    for (ParticleData particle : algorithm.getParticleData(currentFrame)) {
//                        double x = 1 - particle.x() / (0.5 * Configuration.Lx);
//                        double y = 1 - particle.y() / (0.5 * Configuration.Ly);
//                        GL11.glVertex2d(x, y);
//                    }
//                    GL11.glEnd();
//                    shaderProgram.unbind();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                shaderProgram.bind();
                {
                    shaderProgram.setUniform("projectionMatrix", projectionMatrix);
                    shaderProgram.setUniform("viewMatrix", viewMatrix);
//                    shaderProgram.setUniform("width", (float) width);
                    mesh.render();
                }
                shaderProgram.unbind();

                GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                glfwSwapBuffers(window);
                glfwPollEvents();

                frameStart = frameStop;

                timer += 4;

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
