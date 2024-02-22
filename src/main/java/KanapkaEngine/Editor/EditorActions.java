package KanapkaEngine.Editor;

import KanapkaEngine.Components.Camera;
import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.World;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.SceneManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EditorActions extends Plugin implements MouseListener {
    private Engine engine;
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        World.getCurrent().saveWorld();
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
