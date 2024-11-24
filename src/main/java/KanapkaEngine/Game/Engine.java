package KanapkaEngine.Game;

import KanapkaEngine.Components.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Engine {
    private boolean isRunning = true;

    private long window;

    private EngineConfiguration engineConfiguration = new EngineConfiguration();
    private final GameLogic logic;

    private final Physics physics = new Physics();

    private final Time time = new Time();

    private final List<Plugin> plugins = new ArrayList<>();

    private long last_fixed_update = System.nanoTime();
    private final double Second = (long) Math.pow(10, 9);

    public Engine(GameLogic logic) {
        this.logic = logic;
        init();
    }

    public Engine(GameLogic logic, EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.logic = logic;
        init();
    }

    private void credits() {
        for (int i = 0; i < 10; i++)
            System.out.print("-");
        System.out.println();
        System.out.println();
        System.out.println("Thank you for using KanapkaEngine!");
        System.out.println();
        for (int i = 0; i < 10; i++)
            System.out.print("-");
        System.out.println();
    }

    private void init() {
        credits();

        InitializeLWJGL();
        InitializeOpenGLUpdate();
    }

    public final void load(Plugin plugin) {
        plugins.add(plugin);
        plugin.Apply(this);
    }

    private void Draw() {

    }

    private void Update() {
        time.GameUpdate();
        double start_time = Time.time();
        int target_fps = Math.max(engineConfiguration.FPSLIMIT, 30);
        double TARGET_MS = 1000.0 / target_fps;
        try {
            try {
                SceneManager.getSceneNodes().removeIf((node) -> !node.isAlive());
            } catch (ConcurrentModificationException e) {

            }
            if (last_fixed_update + Second / 50L < System.nanoTime()) {
                double fixedDelta = (System.nanoTime() - last_fixed_update) / Second;
                physics.FixedUpdate(fixedDelta);
                last_fixed_update = System.nanoTime();
            }
            logic.Update();
            for (int i = plugins.size() - 1; i >= 0; i--) {
                plugins.get(i).Update();
            }
            Chunk.UpdateChunks();
            try {
                SceneManager.getSceneNodes().foreach(Node::UpdateCall);
            } catch (ConcurrentModificationException ignore) {

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (engineConfiguration.FPSLIMIT > 0) {
            double updateTime = Time.time() - start_time;
            double ms = TARGET_MS - updateTime;
            if (ms > 1.0)
                try {
                    Thread.sleep((long)ms * 1000);
                } catch (InterruptedException e) {

                }
        }
    }

    public final void End() {
        isRunning = false;

        for (AttributeBuffer buffer : AttributeBuffer.LoadedAttributeBuffers) {
            buffer.Dispose();
        }

        for (Plugin plugin : plugins) {
            plugin.Detach();
        }

        glfwDestroyWindow(window);

        glfwTerminate();

        logic.End();
    }

    /**
     * There are inconsistencies when it comes to window size on a mac and windows computers. Mac window size get calls are 2x smaller for some reason.
     * @return If the operating system is a mac.
     */
    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void InitializeLWJGL() {
        if (!glfwInit()) {
            throw new RuntimeException("Problem while initializing GLFW");
        }

        window = glfwCreateWindow(engineConfiguration.width, engineConfiguration.height, engineConfiguration.window_title, NULL, NULL);

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_VERSION_MINOR, 3);

        if (window == NULL) {
            throw new RuntimeException("Problem while creating GLFW window.");
        }

        glfwSetKeyCallback(window, this::KeyCallBack);

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        logic.Start();

        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    private void InitializeOpenGLUpdate() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Update();

            Draw();

            glfwSwapBuffers(window);

            glfwPollEvents();
        }

        End();
    }

    private void KeyCallBack(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    private void ErrorCheck() {
        PointerBuffer buffer = PointerBuffer.allocateDirect(1024);
        int code;
        while ((code = glfwGetError(buffer)) != GLFW_NO_ERROR) {

        }
    }
}