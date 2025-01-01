package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;

public final class Window {
    private long window;

    private int width;
    private int height;

    Window(long window, EngineConfiguration configuration) {
        this.window = window;

        int[] widthptr = new int[1];
        int[] heightptr = new int[1];

        glfwGetWindowSize(window, widthptr, heightptr);

        width = widthptr[0];
        height = heightptr[0];

        glfwSetWindowSizeCallback(window, this::WindowSizeCallBack);

        setTitle(configuration.window_title);
    }

    private void WindowSizeCallBack(long window, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
    }

    long getGLFWwindow() {
        return window;
    }

    public void setTitle(String text) {
        glfwSetWindowTitle(window, text);
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(window, width, height);
    }

    public void setLocation(int x, int y) {
        glfwSetWindowPos(window, x, y);
    }

    public void setDimensions(int x, int y, int width, int height) {
        setLocation(x, y);
        setSize(width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setIcon() {

    }
}
