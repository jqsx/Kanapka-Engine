package KanapkaEngine.Game;

import KanapkaEngine.Game.Node;
import KanapkaEngine.Game.Transformation;
import org.joml.Vector2d;

import java.awt.geom.AffineTransform;

public class Transform {
    private final Transformation transformation;
    public final Node parent;
    private final Vector2d position = new Vector2d(0, 0);
    private double rotation = 0;
    private final Vector2d size = new Vector2d(1, 1);

    protected Transform(Node parent) {
        this.parent = parent;
        transformation = new Transformation(getPosition(), new Vector2d(1, 1), 0.f);
    }

    public Vector2d getPosition() {
        Node node_parent = parent.getParent();
        if (node_parent != null) {
            return position.add(node_parent.transform.getPosition());
        }
        return position;
    }

    public Vector2d getLocalPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        Node node_parent = parent.getParent();
        if (node_parent != null) {
            this.position.set(node_parent.transform.getPosition().sub(position));
        }
        else
            this.position.set(position);
    }

    public void setPosition(double x, double y) {
        Node node_parent = parent.getParent();
        if (node_parent != null) {
            this.position.set(node_parent.transform.getPosition().sub(x, y));
        }
        else
            this.position.set(x, y);
    }

    public Vector2d getSize() {
        return size;
    }

    public void setSize(Vector2d size) {
        this.size.set(size);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation % (Math.PI * 2);
    }

    public Transformation getTransformation() {
        transformation.Update(getPosition(), size, (float) rotation);

        return transformation;
    }
}