package game;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final int width;
    private final int height;
    private final String title;

    private long handle;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        glfwDefaultWindowHints();

        // Keep compatibility mode.
        // Do not request OpenGL Core Profile because the project still uses GL11 immediate mode.
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // Conservative context for old GL11 / fixed pipeline.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL) {
            throw new RuntimeException("Failed to create GLFW window.");
        }

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1); // VSync

        glfwShowWindow(handle);

        // Required after glfwMakeContextCurrent and before any OpenGL call.
        GL.createCapabilities();
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void requestClose() {
        glfwSetWindowShouldClose(handle, true);
    }

    public void destroy() {
        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public long getHandle() {
        return handle;
    }
}