package KanapkaEngine.Components;

import KanapkaEngine.Game.ICollider;
import org.joml.Vector2d;

import java.awt.geom.Rectangle2D;

/**
 * Standard AABB box collider. The collider scales along with the object size and can have an additional offset and size as well.
 */
public class RectangleCollider extends ICollider {
    private Vector2d size = new Vector2d(1, 1);
    private Vector2d offset = new Vector2d(0, 0);

    private Rectangle2D.Double rect = new Rectangle2D.Double();

    public boolean collideNodes = true;

    public boolean noMass = false;

    public RectangleCollider() {

    }

    public RectangleCollider(Vector2d size) {
        setSize(size);
    }

    public Vector2d getSize() {
        return size;
    }

    public void setSize(Vector2d size) {
        this.size = new Vector2d(Math.abs(size.x), Math.abs(size.y));
        getRectangle();
    }

    public boolean isColliding(RectangleCollider other) {
        return getRectangle().intersects(other.getRectangle());
    }

    @Deprecated
    public Rectangle2D _getRectangle() {
        Vector2d scaledSize = getScaledSize();
        double w = scaledSize.x;
        double h = scaledSize.y;

        double x = (offset.x + (getParent().transform2D().getPosition().x + getParent().transform2D().getSize().x / 2.0) - scaledSize.x / 2.0);
        double y = (offset.y + getParent().transform2D().getPosition().y - scaledSize.y / 2.0);

        rect.setRect(x, y, w, h);
        return rect;
    }

    public Rectangle2D.Double getRectangle() {
        Vector2d scaledSize = getScaledSize();
        double w = scaledSize.x;
        double h = scaledSize.y;

        double x = (offset.x + getParent().transform2D().getPosition().x - scaledSize.x / 2.0);
        double y = (offset.y + getParent().transform2D().getPosition().y - scaledSize.y / 2.0);

        rect.setRect(x, y, w, h);
        return rect;
    }

    public Vector2d getScaledSize() {
        return new Vector2d(size.x * getParent().transform2D().getSize().x, size.y * getParent().transform2D().getSize().y);
    }

    public Vector2d getOffset() {
        return offset;
    }

    public void setOffset(Vector2d offset) {
        this.offset = offset;
        getRectangle();
    }
}
