package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EditorActions extends Plugin implements MouseListener {
    private Engine engine;
    @Override
    public void mouseClicked(MouseEvent e) {
        {
            Node node = new Node();

            node.addComponent(new Renderer());

            node.getRenderer().setTexture(ResourceLoader.loadResource("none.png"));

            node.transform.setSize(new Vector2D(16, 16));
            node.transform.setPosition(Camera.main.getPosition().scalarMultiply(-1));

            node.addComponent(new Rigidbody());

            node.addComponent(new Collider());

            node.append();
        }
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
