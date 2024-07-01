package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.Rectangle2D;

/**
 * Standard AABB box collider. The collider scales along with the object size and can have an additional offset and size as well.
 */
public class Collider extends Component {
    private Vector2D size = new Vector2D(1, 1);
    private Vector2D offset = new Vector2D(0, 0);

    private Rectangle2D rect = new Rectangle2D.Double();

    public boolean collideNodes = true;

    public boolean noMass = false;

    public Collider() {

    }

    public Collider(Vector2D size) {
        setSize(size);
    }

    public Vector2D getSize() {
        return size;
    }

    public void setSize(Vector2D size) {
        this.size = new Vector2D(Math.abs(size.getX()), Math.abs(size.getY()));
        getRectangle();
    }

    public boolean isColliding(Collider other) {
        return getRectangle().intersects(other.getRectangle());
    }

    public Rectangle2D getRectangle() {
        Vector2D scaledSize = getScaledSize();
        double w = scaledSize.getX();
        double h = scaledSize.getY();

        double x = (offset.getX() + (getParent().transform.getPosition().getX() + getParent().transform.getSize().getX() / 2.0) - scaledSize.getX() / 2.0);
        double y = (offset.getY() + getParent().transform.getPosition().getY() - scaledSize.getY() / 2.0);

        rect.setRect(x, y, w, h);
        return rect;
    }

    public Vector2D getScaledSize() {
        return new Vector2D(size.getX() * getParent().transform.getSize().getX(), size.getY() * getParent().transform.getSize().getY());
    }

    public Vector2D getOffset() {
        return offset;
    }

    public void setOffset(Vector2D offset) {
        this.offset = offset;
        getRectangle();
    }
}
