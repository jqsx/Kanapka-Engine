package KanapkaEngine.Game;

import KanapkaEngine.Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Input extends Plugin implements KeyListener {
    private static HashMap<Character, Boolean> keysDown = new HashMap<>();

    private static Input instance;

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
        keysDown.put(e.getKeyChar(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (instance != this) return;
        keysDown.remove(e.getKeyChar());
    }

    public static boolean isKeyDown(char c) {
        return keysDown.containsKey(c);
    }
}
