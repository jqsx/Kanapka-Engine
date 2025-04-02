package KanapkaEngine.Game;

import KanapkaEngine.Components.RenderStage;
import imgui.*;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import imgui.lwjgl3.glfw.ImGuiImplGlfwNative;
import imgui.type.ImInt;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.glfw.GLFW.*;

public final class GuiRenderer implements IInput, ICleanUp, RenderLayer, Plugin {
    static GuiRenderer instance;
    private IDrawGUI drawGUI;

    private final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    public GuiRenderer(IDrawGUI drawGUI) {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE. (Put the initialization of this object in/after the Start method executed in the GameLogic object.)");
        }
        if (instance == null) {
            instance = this;
        }
        else {
            throw new RuntimeException("There can only exist one GuiRenderer object.");
        }
        this.drawGUI = drawGUI;
        Window window = Engine.getMainInstance().getWindow();

        createUIResources(window);
    }

    private void createUIResources(Window window) {
        ImGui.createContext();

        ImGui.styleColorsClassic();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        imGuiIO.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);
        imGuiIO.setBackendPlatformName("imgui_java_impl_glfw");
        imGuiIO.setBackendRendererName("imgui_java_impl_lwjgl");
        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

        final ImFontAtlas fontAtlas = imGuiIO.getFonts();

        fontAtlas.addFontDefault();

        fontAtlas.build();

        implGl3.init();
    }

    @Override
    public void KeyCallBack(int key, int scancode, int action, int mods) {
        ImGuiIO io = ImGui.getIO();
        if (!io.getWantCaptureKeyboard()) {
            return;
        }
        if (action == GLFW_PRESS) {
            io.addKeyEvent(getImKey(key), true);
        } else if (action == GLFW_RELEASE) {
            io.addKeyEvent(getImKey(key), false);
        }
        
    }

    @Override
    public void CharKeyCallback(int c) {
        ImGuiIO io = ImGui.getIO();
        if (!io.getWantCaptureKeyboard()) {
            return;
        }
        io.addInputCharacter(c);
    }

    @Override
    public void ScrollCallback(double x, double y) {
        ImGuiIO io = ImGui.getIO();

        io.setMouseWheelH(io.getMouseWheelH() + (float) x);
        io.setMouseWheel(io.getMouseWheel() + (float) y);
    }

    private static int getImKey(int key) {
        int result;

        switch (key) {
            case GLFW_KEY_TAB: result =  ImGuiKey.Tab;
                break;
            case GLFW_KEY_LEFT: result =  ImGuiKey.LeftArrow;
                break;
            case GLFW_KEY_RIGHT: result =  ImGuiKey.RightArrow;
                break;
            case GLFW_KEY_UP: result =  ImGuiKey.UpArrow;
                break;
            case GLFW_KEY_DOWN: result =  ImGuiKey.DownArrow;
                break;
            case GLFW_KEY_PAGE_UP: result =  ImGuiKey.PageUp;
                break;
            case GLFW_KEY_PAGE_DOWN: result =  ImGuiKey.PageDown;
                break;
            case GLFW_KEY_HOME: result =  ImGuiKey.Home;
                break;
            case GLFW_KEY_END: result =  ImGuiKey.End;
                break;
            case GLFW_KEY_INSERT: result =  ImGuiKey.Insert;
                break;
            case GLFW_KEY_DELETE: result =  ImGuiKey.Delete;
                break;
            case GLFW_KEY_BACKSPACE: result =  ImGuiKey.Backspace;
                break;
            case GLFW_KEY_SPACE: result =  ImGuiKey.Space;
                break;
            case GLFW_KEY_ENTER: result =  ImGuiKey.Enter;
                break;
            case GLFW_KEY_ESCAPE: result =  ImGuiKey.Escape;
                break;
            case GLFW_KEY_APOSTROPHE: result =  ImGuiKey.Apostrophe;
                break;
            case GLFW_KEY_COMMA: result =  ImGuiKey.Comma;
                break;
            case GLFW_KEY_MINUS: result =  ImGuiKey.Minus;
                break;
            case GLFW_KEY_PERIOD: result =  ImGuiKey.Period;
                break;
            case GLFW_KEY_SLASH: result =  ImGuiKey.Slash;
                break;
            case GLFW_KEY_SEMICOLON: result =  ImGuiKey.Semicolon;
                break;
            case GLFW_KEY_EQUAL: result =  ImGuiKey.Equal;
                break;
            case GLFW_KEY_LEFT_BRACKET: result =  ImGuiKey.LeftBracket;
                break;
            case GLFW_KEY_BACKSLASH: result =  ImGuiKey.Backslash;
                break;
            case GLFW_KEY_RIGHT_BRACKET: result =  ImGuiKey.RightBracket;
                break;
            case GLFW_KEY_GRAVE_ACCENT: result =  ImGuiKey.GraveAccent;
                break;
            case GLFW_KEY_CAPS_LOCK: result =  ImGuiKey.CapsLock;
                break;
            case GLFW_KEY_SCROLL_LOCK: result =  ImGuiKey.ScrollLock;
                break;
            case GLFW_KEY_NUM_LOCK: result =  ImGuiKey.NumLock;
                break;
            case GLFW_KEY_PRINT_SCREEN: result =  ImGuiKey.PrintScreen;
                break;
            case GLFW_KEY_PAUSE: result =  ImGuiKey.Pause;
                break;
            case GLFW_KEY_KP_0: result =  ImGuiKey.Keypad0;
                break;
            case GLFW_KEY_KP_1: result =  ImGuiKey.Keypad1;
                break;
            case GLFW_KEY_KP_2: result =  ImGuiKey.Keypad2;
                break;
            case GLFW_KEY_KP_3: result =  ImGuiKey.Keypad3;
                break;
            case GLFW_KEY_KP_4: result =  ImGuiKey.Keypad4;
                break;
            case GLFW_KEY_KP_5: result =  ImGuiKey.Keypad5;
                break;
            case GLFW_KEY_KP_6: result =  ImGuiKey.Keypad6;
                break;
            case GLFW_KEY_KP_7: result =  ImGuiKey.Keypad7;
                break;
            case GLFW_KEY_KP_8: result =  ImGuiKey.Keypad8;
                break;
            case GLFW_KEY_KP_9: result =  ImGuiKey.Keypad9;
                break;
            case GLFW_KEY_KP_DECIMAL: result =  ImGuiKey.KeypadDecimal;
                break;
            case GLFW_KEY_KP_DIVIDE: result =  ImGuiKey.KeypadDivide;
                break;
            case GLFW_KEY_KP_MULTIPLY: result =  ImGuiKey.KeypadMultiply;
                break;
            case GLFW_KEY_KP_SUBTRACT: result =  ImGuiKey.KeypadSubtract;
                break;
            case GLFW_KEY_KP_ADD: result =  ImGuiKey.KeypadAdd;
                break;
            case GLFW_KEY_KP_ENTER: result =  ImGuiKey.KeypadEnter;
                break;
            case GLFW_KEY_KP_EQUAL: result =  ImGuiKey.KeypadEqual;
                break;
            case GLFW_KEY_LEFT_SHIFT: result =  ImGuiKey.LeftShift;
                break;
            case GLFW_KEY_LEFT_CONTROL: result =  ImGuiKey.LeftCtrl;
                break;
            case GLFW_KEY_LEFT_ALT: result =  ImGuiKey.LeftAlt;
                break;
            case GLFW_KEY_LEFT_SUPER: result =  ImGuiKey.LeftSuper;
                break;
            case GLFW_KEY_RIGHT_SHIFT: result =  ImGuiKey.RightShift;
                break;
            case GLFW_KEY_RIGHT_CONTROL: result =  ImGuiKey.RightCtrl;
                break;
            case GLFW_KEY_RIGHT_ALT: result =  ImGuiKey.RightAlt;
                break;
            case GLFW_KEY_RIGHT_SUPER: result =  ImGuiKey.RightSuper;
                break;
            case GLFW_KEY_MENU: result =  ImGuiKey.Menu;
                break;
            case GLFW_KEY_0: result =  ImGuiKey._0;
                break;
            case GLFW_KEY_1: result =  ImGuiKey._1;
                break;
            case GLFW_KEY_2: result =  ImGuiKey._2;
                break;
            case GLFW_KEY_3: result =  ImGuiKey._3;
                break;
            case GLFW_KEY_4: result =  ImGuiKey._4;
                break;
            case GLFW_KEY_5: result =  ImGuiKey._5;
                break;
            case GLFW_KEY_6: result =  ImGuiKey._6;
                break;
            case GLFW_KEY_7: result =  ImGuiKey._7;
                break;
            case GLFW_KEY_8: result =  ImGuiKey._8;
                break;
            case GLFW_KEY_9: result =  ImGuiKey._9;
                break;
            case GLFW_KEY_A: result =  ImGuiKey.A;
                break;
            case GLFW_KEY_B: result =  ImGuiKey.B;
                break;
            case GLFW_KEY_C: result =  ImGuiKey.C;
                break;
            case GLFW_KEY_D: result =  ImGuiKey.D;
                break;
            case GLFW_KEY_E: result =  ImGuiKey.E;
                break;
            case GLFW_KEY_F: result =  ImGuiKey.F;
                break;
            case GLFW_KEY_G: result =  ImGuiKey.G;
                break;
            case GLFW_KEY_H: result =  ImGuiKey.H;
                break;
            case GLFW_KEY_I: result =  ImGuiKey.I;
                break;
            case GLFW_KEY_J: result =  ImGuiKey.J;
                break;
            case GLFW_KEY_K: result =  ImGuiKey.K;
                break;
            case GLFW_KEY_L: result =  ImGuiKey.L;
                break;
            case GLFW_KEY_M: result =  ImGuiKey.M;
                break;
            case GLFW_KEY_N: result =  ImGuiKey.N;
                break;
            case GLFW_KEY_O: result =  ImGuiKey.O;
                break;
            case GLFW_KEY_P: result =  ImGuiKey.P;
                break;
            case GLFW_KEY_Q: result =  ImGuiKey.Q;
                break;
            case GLFW_KEY_R: result = ImGuiKey.R;
                break;
            case GLFW_KEY_S: result =  ImGuiKey.S;
                break;
            case GLFW_KEY_T: result =  ImGuiKey.T;
                break;
            case GLFW_KEY_U: result =  ImGuiKey.U;
                break;
            case GLFW_KEY_V: result =  ImGuiKey.V;
                break;
            case GLFW_KEY_W: result =  ImGuiKey.W;
                break;
            case GLFW_KEY_X: result =  ImGuiKey.X;
                break;
            case GLFW_KEY_Y: result =  ImGuiKey.Y;
                break;
            case GLFW_KEY_Z: result =  ImGuiKey.Z;
                break;
            case GLFW_KEY_F1: result =  ImGuiKey.F1;
                break;
            case GLFW_KEY_F2: result =  ImGuiKey.F2;
                break;
            case GLFW_KEY_F3: result =  ImGuiKey.F3;
                break;
            case GLFW_KEY_F4: result =  ImGuiKey.F4;
                break;
            case GLFW_KEY_F5: result =  ImGuiKey.F5;
                break;
            case GLFW_KEY_F6: result =  ImGuiKey.F6;
                break;
            case GLFW_KEY_F7: result =  ImGuiKey.F7;
                break;
            case GLFW_KEY_F8: result =  ImGuiKey.F8;
                break;
            case GLFW_KEY_F9: result = ImGuiKey.F9;
                break;
            case GLFW_KEY_F10: result =  ImGuiKey.F10;
                break;
            case GLFW_KEY_F11: result =  ImGuiKey.F11;
                break;
            case GLFW_KEY_F12: result =  ImGuiKey.F12;
            break;
            default: result = ImGuiKey.None;
        }

        return result;
    }

    @Override
    public void cleanUp() {
        implGl3.shutdown();
        ImGui.destroyContext();
    }

    @Override
    public void Render() {
        if (drawGUI == null)
            return;

        Window window = Engine.getMainInstance().getWindow();
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

        imGuiIO.addMousePosEvent(Input.getMousePosition().x, Input.getMousePosition().y);
        imGuiIO.addMouseButtonEvent(0, Input.isButtonDown(GLFW_MOUSE_BUTTON_LEFT, true));
        imGuiIO.addMouseButtonEvent(1, Input.isButtonDown(GLFW_MOUSE_BUTTON_RIGHT, true));
        imGuiIO.setDeltaTime((float) Time.deltaTime());

        ImGui.newFrame();
        implGl3.newFrame();
        drawGUI.RenderGUI();
        ImGui.render();

        implGl3.renderDrawData(ImGui.getDrawData());
    }

    public static void ImGuiImage(Texture texture) {
        ImGui.image(texture.textureId, texture.width, texture.height);
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }

    @Override
    public void Apply(Engine engine) {

    }

    @Override
    public void Update() {

    }

    @Override
    public void Detach() {

    }
}
