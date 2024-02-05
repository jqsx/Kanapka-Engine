package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

public class Collider extends NodeComponent {
    private Vector2D size = new Vector2D(1, 1);
    private Vector2D offset = new Vector2D(0, 0);
    private final Rectangle rectangle = new Rectangle();
    public Vector2D getSize() {
        return size;
    }

    public void setSize(Vector2D size) {
        this.size = new Vector2D(Math.abs(size.getX()), Math.abs(size.getY()));
        getRectangle();
    }

    public boolean isColliding(Collider other) {
        return other.getRectangle().intersects(getRectangle());
    }

    private Rectangle getRectangle() {
        rectangle.width = (int)Math.round(size.getX() * 16.0);
        rectangle.height = (int)Math.round(size.getY() * 16.0);

        rectangle.x = (int)Math.round((offset.getX() + getParent().transform.getPosition().getX() - size.getX() / 2.0) * 16.0);
        rectangle.y = (int)Math.round((offset.getY() + getParent().transform.getPosition().getY() - size.getY() / 2.0) * 16.0);

        return rectangle;
    }

    public Vector2D getOffset() {
        return offset;
    }

    public void setOffset(Vector2D offset) {
        this.offset = offset;
        getRectangle();
    }
}
