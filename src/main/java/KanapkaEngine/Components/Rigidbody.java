package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Rigidbody extends NodeComponent {
    private Vector2D velocity = new Vector2D(0,0);

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D addVelocity(Vector2D force) {
        return velocity = velocity.add(force);
    }
}
