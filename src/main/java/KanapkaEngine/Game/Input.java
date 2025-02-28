package KanapkaEngine.Game;

import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Components.TSLinkedList;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Input implements Plugin {
    private static final Set<Integer> keysDown = new HashSet<>();

    private static Vector2i mousePosition = new Vector2i(0, 0);

    private static final Set<Integer> buttonDown = new HashSet<>();
    private static final Set<Integer> keyDownFrame = new HashSet<>();
    private static final Set<Integer> keyUpFrame = new HashSet<>();

    private static Vector2d mouseWorldLocation = new Vector2d();

    Input() {

    }

    void InputReset() {
        keyDownFrame.clear();
        keyUpFrame.clear();
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

    void keyPressed(int key) {
        if (!keysDown.contains(key))
            keysDown.add(key);

        keyDownFrame.add(key);
    }

    void keyReleased(int key) {
        keysDown.remove(key);
    }

    public static boolean isKeyDown(int c, boolean ignoreImGui) {
        ImGuiIO io = ImGui.getIO();

        return (!io.getWantCaptureKeyboard() || ignoreImGui) && keysDown.contains(c);
    }

    public static boolean isKeyDown(int c) {
        return isKeyDown(c, false);
    }

    public static boolean isButtonDown(int b, boolean ignoreImGui) {
        ImGuiIO io = ImGui.getIO();

        return (!io.getWantCaptureMouse() || ignoreImGui) && buttonDown.contains(b);
    }

    public static boolean isButtonDown(int b) {
        return isButtonDown(b, false);
    }

    public static boolean isKeyDownFrame(int c) { return keyDownFrame.contains(c); }
    public static boolean isKeyUpFrame(int c) { return keyUpFrame.contains(c); }

    void mouseMoved(int x, int y) {
        mousePosition.set(x,y);
    }

    public static Vector2i getMousePosition() {
        return mousePosition;
    }

    public static Vector2d getWorldMousePosition() {
        Window window = Engine.getMainInstance().getWindow();

        float ratio = window.getWidth() / (float)window.getHeight();

        float reverseRatio = 1.f / ratio;

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 20.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 20.0);
        mouseWorldLocation.set((mousePosition.x / (double)window.getWidth() * 2 - 1) * Camera.main.size * widthMult, (-mousePosition.y / (double)window.getHeight() * 2 + 1) * Camera.main.size * heightMult).add(Camera.main.getPosition());

        return mouseWorldLocation;
    }

    void mousePressed(int button) {
        if (!buttonDown.contains(button))
            buttonDown.add(button);
    }

    void mouseReleased(int button) {
        buttonDown.remove(button);
    }
}
