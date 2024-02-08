package KanapkaEngine.Editor;

import KanapkaEngine.Components.Node;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.SceneManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EditorActions extends Plugin implements MouseListener {
    private Engine engine;
    public static boolean log = false;
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SceneManager.hasScene())
            for (Node node : SceneManager.getSceneNodes()) {
                if (node.getCollider() != null)
                    System.out.println(node.getCollider().getRectangle().toString());
            }

        log = true;
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
