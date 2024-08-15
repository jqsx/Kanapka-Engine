package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.Random;

public class EditorActions extends Plugin implements MouseListener {
    private Engine engine;

    private LinkedList<Node> sandwiches = new LinkedList<>();

    private double delay = 0.0;
    @Override
    public void mouseClicked(MouseEvent e) {
        {
            Node node = new Node();

            node.addComponent(new Renderer());

            node.getRenderer().setTexture(ResourceLoader.loadResource("wooden.png"));

            Random random = new Random();

            node.transform.setSize(new Vector2D(16 + random.nextInt(16), 16 + random.nextInt(16)));
            node.transform.setPosition(Camera.main.getWorldPosition());

            node.addComponent(new Rigidbody());

            node.addComponent(new Collider());

            node.append();

            sandwiches.add(node);
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
        if (delay < Time.time()) {
            delay = Time.time() + 1.0 / 50.0;

            sandwiches.forEach(sandwich -> {
                Rigidbody rb = sandwich.getRigidbody();

                if (rb != null) {
                    Vector2D diff = Camera.main.getWorldPosition().subtract(sandwich.transform.getPosition());

                    diff = diff.scalarMultiply(1.0 / Mathf.aDistance(Vector2D.ZERO, diff));
                    rb.setVelocity(rb.getVelocity().add(diff.scalarMultiply((1.0 / 50.0) * 30)));
                }
            });
        }
    }

    @Override
    public void Detach() {

    }
}
