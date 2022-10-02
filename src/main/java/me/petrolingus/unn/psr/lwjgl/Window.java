package me.petrolingus.unn.psr.lwjgl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width;

    private int height;

    private long window;

    private Renderer renderer;

    public Window(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void run()  {

        initialize();
        try {
            loop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void stop() {
        renderer.stop();
    }

    public synchronized ByteBuffer getBuffer() {
        return renderer.getBuffer();
    }

    public boolean isRunning() {
        return renderer != null && renderer.isRunning();
    }

    private void initialize() {

        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 16);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 16);

        // Create the window
        window = glfwCreateWindow(width, height, "Window Title", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            int xPos = (vidMode.width() - pWidth.get(0)) / 2;
            int yPos = (vidMode.height() - pHeight.get(0)) / 2;
            glfwSetWindowPos(window, xPos, yPos);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window not visible
//        glfwShowWindow(window);
        glfwHideWindow(window);

        renderer = new Renderer(width, height, window);
    }

    private void loop() throws Exception {
        renderer.run();
    }
}
