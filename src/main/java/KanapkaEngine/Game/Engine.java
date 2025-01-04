package KanapkaEngine.Game;

import KanapkaEngine.Components.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class Engine {
    private static final Logger logger = new Logger("ENGINE");
    private static Engine instance;

    static boolean openglready = false;

    private boolean isRunning = true;

    private long window;

    private Window WindowObject;

    private EngineConfiguration engineConfiguration = new EngineConfiguration();
    private final GameLogic logic;

    private final Physics physics = new Physics();

    private final Time time = new Time();

    private final List<Plugin> plugins = new ArrayList<>();

    private long last_fixed_update = System.nanoTime();
    private final double Second = (long) Math.pow(10, 9);

    private Input input;

    private RenderTexture globalTexture;

    private final List<RenderLayer> BACKGROUND = new ArrayList<>();
    private final List<RenderLayer> WORLD = new ArrayList<>();
    private final List<RenderLayer> PARTICLES = new ArrayList<>();
    private final List<RenderLayer> UI = new ArrayList<>();
    private final List<RenderLayer> FOREGROUND = new ArrayList<>();

    public Engine(GameLogic logic) {
        this.logic = logic;
        init();
    }

    public Engine(GameLogic logic, EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.logic = logic;
        init();
    }

    static boolean isOpenGLInitialized() {
        return openglready;
    }

    private void credits() {
        BufferedImage image = ResourceLoader.loadResource("logo.png");

        BufferedImage logo = new BufferedImage(32, 16, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = logo.createGraphics();

        AffineTransform at = new AffineTransform();

        at.scale(logo.getWidth() / (double)image.getWidth(), logo.getHeight() / (double)image.getHeight());

        g.drawImage(image, at, null);

        g.dispose();

        for (int y = 0; y < logo.getHeight(); y++) {
            for (int x = 0; x < logo.getWidth(); x++) {
                Color color = new Color(logo.getRGB(x, y));

                System.out.print(ANSI.getAnsiColor(color.getRed(), color.getGreen(), color.getBlue()) + "█");
            }
            System.out.println();
        }

        image.flush();
        logo.flush();
        logger.log("Thank you for using Kanapka Engine.");
    }

    private void init() {
        instance = this;
        credits();

        load(input = new Input());
        load(new Scheduler());

        try {
            InitializeLWJGL();
            InitializeOpenGLUpdate();
        } catch (Exception e) {

            logger.error("THE ENGINE HAS CRASHED.");
            logger.error("FREEING ALL OPENGL DATA");

            End();

            throw new RuntimeException(e);
        }
    }

    public void load(Plugin plugin) {
        plugins.add(plugin);
        plugin.Apply(this);
    }

    public Window getWindow() {
        return WindowObject;
    }

    private TextureMaterial textureMaterial;
    private Shader invert;

    private void Draw() {
        Camera.createProjectionMatrix(WindowObject.getWidth() / (float)WindowObject.getHeight());

        if (textureMaterial == null)
            textureMaterial = new TextureMaterial();

        if (invert == null) {
            invert = Shader.findOrCreate("builtin:post:invert", "Shader/standard/PostProcess/Invert");
        }
//
//        globalTexture.bind();
//
//        globalTexture.clear();

        Render_Layer(BACKGROUND);
        Render_Layer(WORLD);
        Render_Layer(PARTICLES);
        Render_Layer(UI);
        Render_Layer(FOREGROUND);

        globalTexture.unbind();

//        Graphics.DrawFullScreen(globalTexture.getTexture(), invert);
    }

    private void Update()  {
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
                last_fixed_update = System.nanoTime();
                physics.FixedUpdate(fixedDelta);
            }
            logic.Update();
            for (int i = plugins.size() - 1; i >= 0; i--) {
                plugins.get(i).Update();
            }
            Chunk.UpdateChunks();
            try {
                SceneManager.getSceneNodes().forEach(Node::UpdateCall);
            } catch (ConcurrentModificationException ignore) {

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ErrorCheck("End of Frame");

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

    void scroll_callback(long window, double xoffset, double yoffset)
    {
        plugins.forEach(plugin -> {
            if (plugin instanceof IInput in)
                in.ScrollCallback(xoffset, yoffset);
        });
    }


    public void End() {
        isRunning = false;
        openglready = false;

        logger.log("Engine stopped running, freeing OpenGL data.");

        logger.log("Freeing attribute buffers.");
        for (AttributeBuffer buffer : AttributeElementBuffer.LoadedAttributeBuffers) {
            buffer.Dispose(false);
        }
        logger.log("Freed attribute buffers.");

        logger.log("Freeing shaders.");
        for (Shader shader : Shader.LoadedShaders.values())
            shader.Dispose();
        logger.log("Freed shaders.");

        logger.log("Freeing loaded textures.");
        for (Texture texture : Texture.LoadedTextures)
            texture.Dispose(false);
        logger.log("Freed loaded textures.");

        Shader.LoadedShaders.clear();

        AttributeElementBuffer.LoadedAttributeBuffers.clear();

        Texture.LoadedTextures.clear();

        logger.log("Detaching Plugins.");
        for (Plugin plugin : plugins) {
            if (plugin instanceof ICleanUp iCleanUp)
                iCleanUp.cleanUp();
            plugin.Detach();
        }
        logger.log("Detached Plugins.");

        glfwDestroyWindow(window);
        logger.log("Destroyed GLFW window.");

        glfwTerminate();
        logger.log("Terminated GLFW.");

        logger.log("Running end logic.");
        logic.End();
        logger.log("Finished running end logic.");
    }

    @Deprecated
    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void InitializeLWJGL() {
        if (!glfwInit()) {
            throw new RuntimeException("Problem while initializing GLFW");
        }
        logger.log("Initialized GLFW");

        window = glfwCreateWindow(engineConfiguration.width, engineConfiguration.height, engineConfiguration.window_title, NULL, NULL);

        // glfwDefaultWindowHints(); // optional, the current window hints are already the default

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_VERSION_MINOR, 2);
//
//        if (isMacOS()) {
//            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//        }

        if (window == NULL) {
            throw new RuntimeException("Problem while creating GLFW window.");
        }

        logger.log("Initialized GLFW window");

        WindowObject = new Window(window, engineConfiguration);

        glfwSetKeyCallback(window, this::KeyCallBack);
        glfwSetCharCallback(window, this::CharKeyCallback);
        glfwSetCursorPosCallback(window, this::MouseCallback);
        glfwSetMouseButtonCallback(window, this::MouseButtonCallback);
        glfwSetScrollCallback(window, this::scroll_callback);

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

        glfwShowWindow(window);

        GL.createCapabilities();

        openglready = true;

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        globalTexture = new RenderTexture(1920, 1080);

        Shader.Standard.init();

        logger.log("Initialized LWJGL, ready to draw");

        Camera.main = new Camera();

        logic.Start(this);

        ErrorCheck("General");

        logger.log("Finished Initialization");
    }

    private void setDefaultRenderRules() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void InitializeOpenGLUpdate() {
        while (!glfwWindowShouldClose(window) && isRunning) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Update();

            Draw();

            glfwSwapBuffers(window);

            input.InputReset();
            glfwPollEvents();
        }

        End();
    }

    private void KeyCallBack(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            input.keyPressed(key);
        } else if (action == GLFW_RELEASE) {
            input.keyReleased(key);
        }

        plugins.forEach(plugin -> {
            if (plugin instanceof IInput in)
                in.KeyCallBack(key, scancode, action, mods);
        });
    }

    private void CharKeyCallback(long window, int c) {
        plugins.forEach(plugin -> {
            if (plugin instanceof IInput in)
                in.CharKeyCallback(c);
        });
    }

    private void MouseCallback(long window, double x, double y) {
        input.mouseMoved((int) x, (int) y);
    }

    private void MouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            input.mousePressed(button);
        }
        else if (action == GLFW_RELEASE) {
            input.mouseReleased(button);
        }
    }

    public static void ErrorCheck(String namespace) {
        PointerBuffer buffer = PointerBuffer.allocateDirect(1024);
        int code;
        while ((code = glfwGetError(buffer)) != GLFW_NO_ERROR) {
            logger.error("GLFW ERROR > " + namespace + " > CODE " + code);
        }
    }

    static void registerLayer(RenderLayer layer) {
        instance.RegisterRenderLayer(layer, layer.getStage());
    }

    private void Render_Layer(List<RenderLayer> renderStage) {
        for (RenderLayer renderLayer : renderStage) {
            setDefaultRenderRules();
            renderLayer.Render();
        }
    }

    private void RegisterRenderLayer(RenderLayer renderLayer, RenderStage renderStage) {
        Objects.requireNonNull(renderLayer);
        Objects.requireNonNull(renderStage);
        switch (renderStage) {
            case BACKGROUND -> BACKGROUND.add(renderLayer);
            case WORLD -> WORLD.add(renderLayer);
            case PARTICLES -> PARTICLES.add(renderLayer);
            case UI -> UI.add(renderLayer);
            case FOREGROUND -> FOREGROUND.add(renderLayer);
        }
    }

    public static Engine getMainInstance() {
        return instance;
    }
}