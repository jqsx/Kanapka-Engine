package KanapkaEngine.Components;

import KanapkaEngine.Game.Time;
import org.joml.Vector2d;

public class Particle {
    private Vector2d position = new Vector2d(0.0, 0.0);
    private Vector2d velocity = new Vector2d(0.0, 0.0);

    private final double birth = Time.time();

    public Particle(Vector2d position) {
        this.position = position;
    }

    public final boolean isDead(double lifeTime) {
        return birth + lifeTime < Time.time();
    }

    public final Vector2d getVelocity() {
        return velocity;
    }

    public final void setVelocity(Vector2d velocity) {
        this.velocity = velocity;
    }

    public final Vector2d getPosition() {
        return position;
    }

    public final void setPosition(Vector2d position) {
        this.position = position;
    }

    public final void addPosition(Vector2d add) {
        position = position.add(add);
    }

    public final void addVelocity(Vector2d add) {
        velocity = velocity.add(add);
    }
}
