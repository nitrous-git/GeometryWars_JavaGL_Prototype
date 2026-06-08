package game;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final int width;
    private final int height;
    private final String title;

    private long handle;

    private int windowWidth;
    private int windowHeight;

    private int framebufferWidth;
    private int framebufferHeight;

    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;

    private GLFWFramebufferSizeCallback framebufferSizeCallback;

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

        // Allow the user to resize the window.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Conservative context for old GL11 / fixed pipeline.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL) {
            throw new RuntimeException("Failed to create GLFW window.");
        }

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1); // VSync

        // Required after glfwMakeContextCurrent and before any OpenGL call.
        GL.createCapabilities();

        framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int newFramebufferWidth, int newFramebufferHeight) {
                resizeViewport(newFramebufferWidth, newFramebufferHeight);
            }
        };

        glfwSetFramebufferSizeCallback(handle, framebufferSizeCallback);

        updateWindowSize();
        updateFramebufferSize();
        resizeViewport(framebufferWidth, framebufferHeight);

        glfwShowWindow(handle);
    }

    private void updateWindowSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(handle, w, h);

            windowWidth = w.get(0);
            windowHeight = h.get(0);
        }
    }

    private void updateFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetFramebufferSize(handle, w, h);

            framebufferWidth = w.get(0);
            framebufferHeight = h.get(0);
        }
    }

    private void resizeViewport(int newFramebufferWidth, int newFramebufferHeight) {
        if (newFramebufferWidth <= 0 || newFramebufferHeight <= 0) {
            return;
        }

        framebufferWidth = newFramebufferWidth;
        framebufferHeight = newFramebufferHeight;

        updateWindowSize();

        float targetAspect = (float) Game.WIDTH / (float) Game.HEIGHT;
        float framebufferAspect = (float) framebufferWidth / (float) framebufferHeight;

        if (framebufferAspect > targetAspect) {
            // Window is too wide: pillarbox left/right.
            viewportHeight = framebufferHeight;
            viewportWidth = (int) (framebufferHeight * targetAspect);
            viewportX = (framebufferWidth - viewportWidth) / 2;
            viewportY = 0;
        } else {
            // Window is too tall: letterbox top/bottom.
            viewportWidth = framebufferWidth;
            viewportHeight = (int) (framebufferWidth / targetAspect);
            viewportX = 0;
            viewportY = (framebufferHeight - viewportHeight) / 2;
        }

        glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // Keep the game world fixed at 1280 x 960.
        glOrtho(0, Game.WIDTH, Game.HEIGHT, 0, 1, -1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public double toGameX(double mouseX) {
        if (viewportWidth <= 0 || windowWidth <= 0) {
            return mouseX;
        }

        // Mouse coordinates are in window coordinates.
        // Viewport coordinates are in framebuffer pixels.
        double framebufferMouseX = mouseX * framebufferWidth / (double) windowWidth;
        double gameX = (framebufferMouseX - viewportX) * Game.WIDTH / (double) viewportWidth;

        return clamp(gameX, 0.0, Game.WIDTH);
    }

    public double toGameY(double mouseY) {
        if (viewportHeight <= 0 || windowHeight <= 0) {
            return mouseY;
        }

        double framebufferMouseY = mouseY * framebufferHeight / (double) windowHeight;
        double gameY = (framebufferMouseY - viewportY) * Game.HEIGHT / (double) viewportHeight;

        return clamp(gameY, 0.0, Game.HEIGHT);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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
        if (framebufferSizeCallback != null) {
            framebufferSizeCallback.free();
        }

        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public long getHandle() {
        return handle;
    }
}