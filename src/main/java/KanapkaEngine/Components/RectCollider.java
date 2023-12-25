package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Rectangle;

public class RectCollider extends Collider {
    private BodyFixture fixture;
    private Vector2D size = new Vector2D(1, 1);

    public Vector2D getSize() {
        return size;
    }

    public void setSize(Vector2D size) {
        this.size = size;
    }

    @Override
    public final BodyFixture getFixture() {
        if (fixture == null) {
            fixture = new BodyFixture(Geometry.createRectangle(size.getX(), size.getY()));
            fixture.setDensity(1);
            fixture.setFriction(0.1);
        }
        return fixture;
    }
}
