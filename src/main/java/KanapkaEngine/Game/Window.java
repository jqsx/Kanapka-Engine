package KanapkaEngine.Game;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private long window;

    public int width;
    public int height;

    public Window(long window) {
        this.window = window;

        int[] widthptr = new int[1];
        int[] heightptr = new int[1];

        glfwGetWindowSize(window, widthptr, heightptr);

        width = widthptr[0];
        height = heightptr[0];

        glfwSetWindowSizeCallback(window, this::WindowSizeCallBack);
    }

    private void WindowSizeCallBack(long window, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
    }
}
