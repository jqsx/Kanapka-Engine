package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;
import KanapkaEngine.Engine;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Input extends Plugin implements KeyListener, MouseMotionListener, MouseListener {
    private static final TSLinkedList<Character> keysDown = new TSLinkedList<>();
    private static final TSLinkedList<Integer> keysDownInt = new TSLinkedList<>();

    private static Input instance;

    private Point mousePosition = new Point(0, 0);

    private static final TSLinkedList<Integer> buttonDown = new TSLinkedList<>();

    @Override
    public void Apply(Engine engine) {
        if (instance == null)
            instance = this;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (instance != this) return;
        if (!keysDown.contains(e.getKeyChar()))
            keysDown.addStart(e.getKeyChar());
        if (keysDownInt.contains(e.getKeyCode()))
            keysDownInt.addStart(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (instance != this) return;
        keysDown.remove(e.getKeyChar());
        keysDownInt.remove(e.getKeyCode());
    }

    public static boolean isKeyDown(char c) {
        return keysDown.contains(c);
    }
    public static boolean isKeyDown(int c) {
        return keysDownInt.contains(c);
    }

    public static boolean isButtonDown(int b) {
        return buttonDown.contains(b);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
    }

    public static Vector2D getMousePosition() {
        return new Vector2D(instance.mousePosition.x, instance.mousePosition.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (instance != this) return;
        if (!buttonDown.contains(e.getButton()))
            buttonDown.addStart(e.getButton());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (instance != this) return;
        buttonDown.remove(e.getButton());
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
