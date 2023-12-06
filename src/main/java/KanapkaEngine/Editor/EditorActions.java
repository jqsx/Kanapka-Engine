package KanapkaEngine.Editor;

import KanapkaEngine.Components.AudioClip;
import KanapkaEngine.Components.ResourceLoader;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EditorActions extends Plugin implements MouseListener {
    private Engine engine;
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("AAAA");
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void Apply(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void Update() {

    }

    @Override
    public void Detach() {

    }
}
