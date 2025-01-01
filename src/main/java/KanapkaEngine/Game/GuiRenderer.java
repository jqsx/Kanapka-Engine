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

public final class GuiRenderer extends Plugin implements IInput, ICleanUp, RenderLayer {
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
        return switch (key) {
            case GLFW_KEY_TAB -> ImGuiKey.Tab;
            case GLFW_KEY_LEFT -> ImGuiKey.LeftArrow;
            case GLFW_KEY_RIGHT -> ImGuiKey.RightArrow;
            case GLFW_KEY_UP -> ImGuiKey.UpArrow;
            case GLFW_KEY_DOWN -> ImGuiKey.DownArrow;
            case GLFW_KEY_PAGE_UP -> ImGuiKey.PageUp;
            case GLFW_KEY_PAGE_DOWN -> ImGuiKey.PageDown;
            case GLFW_KEY_HOME -> ImGuiKey.Home;
            case GLFW_KEY_END -> ImGuiKey.End;
            case GLFW_KEY_INSERT -> ImGuiKey.Insert;
            case GLFW_KEY_DELETE -> ImGuiKey.Delete;
            case GLFW_KEY_BACKSPACE -> ImGuiKey.Backspace;
            case GLFW_KEY_SPACE -> ImGuiKey.Space;
            case GLFW_KEY_ENTER -> ImGuiKey.Enter;
            case GLFW_KEY_ESCAPE -> ImGuiKey.Escape;
            case GLFW_KEY_APOSTROPHE -> ImGuiKey.Apostrophe;
            case GLFW_KEY_COMMA -> ImGuiKey.Comma;
            case GLFW_KEY_MINUS -> ImGuiKey.Minus;
            case GLFW_KEY_PERIOD -> ImGuiKey.Period;
            case GLFW_KEY_SLASH -> ImGuiKey.Slash;
            case GLFW_KEY_SEMICOLON -> ImGuiKey.Semicolon;
            case GLFW_KEY_EQUAL -> ImGuiKey.Equal;
            case GLFW_KEY_LEFT_BRACKET -> ImGuiKey.LeftBracket;
            case GLFW_KEY_BACKSLASH -> ImGuiKey.Backslash;
            case GLFW_KEY_RIGHT_BRACKET -> ImGuiKey.RightBracket;
            case GLFW_KEY_GRAVE_ACCENT -> ImGuiKey.GraveAccent;
            case GLFW_KEY_CAPS_LOCK -> ImGuiKey.CapsLock;
            case GLFW_KEY_SCROLL_LOCK -> ImGuiKey.ScrollLock;
            case GLFW_KEY_NUM_LOCK -> ImGuiKey.NumLock;
            case GLFW_KEY_PRINT_SCREEN -> ImGuiKey.PrintScreen;
            case GLFW_KEY_PAUSE -> ImGuiKey.Pause;
            case GLFW_KEY_KP_0 -> ImGuiKey.Keypad0;
            case GLFW_KEY_KP_1 -> ImGuiKey.Keypad1;
            case GLFW_KEY_KP_2 -> ImGuiKey.Keypad2;
            case GLFW_KEY_KP_3 -> ImGuiKey.Keypad3;
            case GLFW_KEY_KP_4 -> ImGuiKey.Keypad4;
            case GLFW_KEY_KP_5 -> ImGuiKey.Keypad5;
            case GLFW_KEY_KP_6 -> ImGuiKey.Keypad6;
            case GLFW_KEY_KP_7 -> ImGuiKey.Keypad7;
            case GLFW_KEY_KP_8 -> ImGuiKey.Keypad8;
            case GLFW_KEY_KP_9 -> ImGuiKey.Keypad9;
            case GLFW_KEY_KP_DECIMAL -> ImGuiKey.KeypadDecimal;
            case GLFW_KEY_KP_DIVIDE -> ImGuiKey.KeypadDivide;
            case GLFW_KEY_KP_MULTIPLY -> ImGuiKey.KeypadMultiply;
            case GLFW_KEY_KP_SUBTRACT -> ImGuiKey.KeypadSubtract;
            case GLFW_KEY_KP_ADD -> ImGuiKey.KeypadAdd;
            case GLFW_KEY_KP_ENTER -> ImGuiKey.KeypadEnter;
            case GLFW_KEY_KP_EQUAL -> ImGuiKey.KeypadEqual;
            case GLFW_KEY_LEFT_SHIFT -> ImGuiKey.LeftShift;
            case GLFW_KEY_LEFT_CONTROL -> ImGuiKey.LeftCtrl;
            case GLFW_KEY_LEFT_ALT -> ImGuiKey.LeftAlt;
            case GLFW_KEY_LEFT_SUPER -> ImGuiKey.LeftSuper;
            case GLFW_KEY_RIGHT_SHIFT -> ImGuiKey.RightShift;
            case GLFW_KEY_RIGHT_CONTROL -> ImGuiKey.RightCtrl;
            case GLFW_KEY_RIGHT_ALT -> ImGuiKey.RightAlt;
            case GLFW_KEY_RIGHT_SUPER -> ImGuiKey.RightSuper;
            case GLFW_KEY_MENU -> ImGuiKey.Menu;
            case GLFW_KEY_0 -> ImGuiKey._0;
            case GLFW_KEY_1 -> ImGuiKey._1;
            case GLFW_KEY_2 -> ImGuiKey._2;
            case GLFW_KEY_3 -> ImGuiKey._3;
            case GLFW_KEY_4 -> ImGuiKey._4;
            case GLFW_KEY_5 -> ImGuiKey._5;
            case GLFW_KEY_6 -> ImGuiKey._6;
            case GLFW_KEY_7 -> ImGuiKey._7;
            case GLFW_KEY_8 -> ImGuiKey._8;
            case GLFW_KEY_9 -> ImGuiKey._9;
            case GLFW_KEY_A -> ImGuiKey.A;
            case GLFW_KEY_B -> ImGuiKey.B;
            case GLFW_KEY_C -> ImGuiKey.C;
            case GLFW_KEY_D -> ImGuiKey.D;
            case GLFW_KEY_E -> ImGuiKey.E;
            case GLFW_KEY_F -> ImGuiKey.F;
            case GLFW_KEY_G -> ImGuiKey.G;
            case GLFW_KEY_H -> ImGuiKey.H;
            case GLFW_KEY_I -> ImGuiKey.I;
            case GLFW_KEY_J -> ImGuiKey.J;
            case GLFW_KEY_K -> ImGuiKey.K;
            case GLFW_KEY_L -> ImGuiKey.L;
            case GLFW_KEY_M -> ImGuiKey.M;
            case GLFW_KEY_N -> ImGuiKey.N;
            case GLFW_KEY_O -> ImGuiKey.O;
            case GLFW_KEY_P -> ImGuiKey.P;
            case GLFW_KEY_Q -> ImGuiKey.Q;
            case GLFW_KEY_R -> ImGuiKey.R;
            case GLFW_KEY_S -> ImGuiKey.S;
            case GLFW_KEY_T -> ImGuiKey.T;
            case GLFW_KEY_U -> ImGuiKey.U;
            case GLFW_KEY_V -> ImGuiKey.V;
            case GLFW_KEY_W -> ImGuiKey.W;
            case GLFW_KEY_X -> ImGuiKey.X;
            case GLFW_KEY_Y -> ImGuiKey.Y;
            case GLFW_KEY_Z -> ImGuiKey.Z;
            case GLFW_KEY_F1 -> ImGuiKey.F1;
            case GLFW_KEY_F2 -> ImGuiKey.F2;
            case GLFW_KEY_F3 -> ImGuiKey.F3;
            case GLFW_KEY_F4 -> ImGuiKey.F4;
            case GLFW_KEY_F5 -> ImGuiKey.F5;
            case GLFW_KEY_F6 -> ImGuiKey.F6;
            case GLFW_KEY_F7 -> ImGuiKey.F7;
            case GLFW_KEY_F8 -> ImGuiKey.F8;
            case GLFW_KEY_F9 -> ImGuiKey.F9;
            case GLFW_KEY_F10 -> ImGuiKey.F10;
            case GLFW_KEY_F11 -> ImGuiKey.F11;
            case GLFW_KEY_F12 -> ImGuiKey.F12;
            default -> ImGuiKey.None;
        };
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

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }
}
