package KanapkaEngine.Components;

import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.Serial;

public class Particle {
    private Vector2D position = new Vector2D(0.0, 0.0);
    private Vector2D velocity = new Vector2D(0.0, 0.0);

    private double birth = Time.time();

    protected Particle(Vector2D position) {
        this.position = position;
    }

    public boolean isDead(double lifeTime) {
        return birth + lifeTime < Time.time();
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void addPosition(Vector2D add) {
        position = position.add(add);
    }

    public void addVelocity(Vector2D add) {
        velocity = velocity.add(add);
    }
}
