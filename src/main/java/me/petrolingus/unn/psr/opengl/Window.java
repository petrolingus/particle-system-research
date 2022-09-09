package me.petrolingus.unn.psr.opengl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Objects;

public class Window {

    private final int width;

    private final int height;

    private boolean initialized = false;

    private long window;

    private Renderer renderer;

    public Window(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void initialize() {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        // Create the window
        window = GLFW.glfwCreateWindow(width, height, "LWJGL Window", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //noinspection resource
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwHideWindow(window);

        this.renderer = new Renderer(width, height, window);
        this.initialized = true;
    }

    public void run() {

        initialize();
        try {
            renderer.loop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    public boolean isInitialize() {
        return initialized;
    }

    public ByteBuffer getBuffer() {
        return renderer.getBuffer();
    }
}
