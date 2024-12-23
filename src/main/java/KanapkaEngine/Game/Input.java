package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Input extends Plugin {
    private static final Set<Integer> keysDown = new HashSet<>();

    private static Vector2i mousePosition = new Vector2i(0, 0);

    private static final Set<Integer> buttonDown = new HashSet<>();
    private static final Set<Integer> keyDownFrame = new HashSet<>();
    private static final Set<Integer> keyUpFrame = new HashSet<>();

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

    void mousePressed(int button) {
        if (!buttonDown.contains(button))
            buttonDown.add(button);
    }

    void mouseReleased(int button) {
        buttonDown.remove(button);
    }
}
