package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private long window;

    private int width;
    private int height;

    public Window(long window) {
        this.window = window;

        glfwSetWindowSizeCallback(window, this::WindowSizeCallBack);
    }

    private void WindowSizeCallBack(long window, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
    }
}
