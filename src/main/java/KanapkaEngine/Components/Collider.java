package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.Rectangle2D;

public class Collider extends Component {
    private Vector2D size = new Vector2D(1, 1);
    private Vector2D offset = new Vector2D(0, 0);

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

        double x = (offset.getX() + getParent().transform.getPosition().getX() - scaledSize.getX() / 2.0);
        double y = (offset.getY() + getParent().transform.getPosition().getY() - scaledSize.getY() / 2.0);

        return new Rectangle2D.Double(x, y, w, h);
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
