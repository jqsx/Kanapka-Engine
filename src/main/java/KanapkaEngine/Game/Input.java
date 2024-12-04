package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public final class Input extends Plugin {
    private static final List<Integer> keysDown = new ArrayList<>();

    private static Vector2i mousePosition = new Vector2i(0, 0);

    private static final List<Integer> buttonDown = new ArrayList<>();
    private static final List<Integer> keyDownFrame = new ArrayList<>();
    private static final List<Integer> keyUpFrame = new ArrayList<>();

    Input() {

    }

    void InputReset() {
        keyDownFrame.clear();
        keyUpFrame.clear();
    }

    @Override
    public void Apply(Engine engine) {

    }

    void keyPressed(int key) {
        if (!keysDown.contains(key))
            keysDown.add(key);

        keyDownFrame.add(key);
    }

    void keyReleased(int key) {
        keysDown.remove(key);
    }

    public static boolean isKeyDown(int c) {
        return keysDown.contains(c);
    }
    public static boolean isButtonDown(int b) {
        return buttonDown.contains(b);
    }

    public static boolean isKeyDownFrame(int c) { return keyDownFrame.contains(c); }
    public static boolean isKeyUpFrame(int c) { return keyUpFrame.contains(c); }

    void mouseMoved(int x, int y) {
        mousePosition.set(x,y);
    }

    public static Vector2i getMousePosition() {
        return mousePosition;
    }

    void mousePressed(int button, int x, int y) {
        if (!buttonDown.contains(button))
            buttonDown.add(button);
    }

    void mouseReleased(int button, int x, int y) {
        buttonDown.remove(button);
    }
}
