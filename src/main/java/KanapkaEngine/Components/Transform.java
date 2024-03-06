package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Transform {
    public final Node parent;
    private Vector2D position = new Vector2D(0, 0);
    private double rotation = 0;
    private Vector2D size = new Vector2D(1, 1);

    protected Transform(Node parent) {
        this.parent = parent;
    }

    public Vector2D getPosition() {
        Node node_parent = parent.getParent();
        if (node_parent != null) {
            assert node_parent.transform != null;
            return position.add(node_parent.transform.getPosition());
        }
        return position;
    }

    public Vector2D getLocalPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        Node node_parent = parent.getParent();
        if (node_parent != null) {
            assert node_parent.transform != null;
            this.position = node_parent.transform.getPosition().subtract(position);
        }
        else
            this.position = position;
    }

    public Vector2D getSize() {
        return size;
    }

    public void setSize(Vector2D size) {
        this.size = size;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}