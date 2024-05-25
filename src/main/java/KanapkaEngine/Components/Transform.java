package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;

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

    public AffineTransform worldToScreenTransform() {
        if (Camera.main == null)
            return null;
        Dimension viewport = Window.getWindowSize();
        double scale = SceneManager.getGlobalSize();
        AffineTransform transform = new AffineTransform();
        Vector2D worldPoint = getPosition();
        Vector2D camera = Camera.main.getPosition();
        transform.translate(worldPoint.getX() - camera.getX(), worldPoint.getY() - camera.getY()); // Translate to camera space
        // Perform combined scaling and rotation in screen space
        transform.concatenate(createRotationAndScaleTransform(size.getX() * scale, size.getY() * scale, rotation));
        transform.translate(viewport.width / 2.0, viewport.height / 2.0); // Center in viewport
        return transform;
    }

    private AffineTransform createRotationAndScaleTransform(double scaleX, double scaleY, double angle) {
        AffineTransform transform = new AffineTransform();
        transform.scale(scaleX, scaleY); // Apply scaling first
        transform.rotate(angle);          // Then rotate
        return transform;
    }
}