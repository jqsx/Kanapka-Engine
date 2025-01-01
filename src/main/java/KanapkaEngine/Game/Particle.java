package KanapkaEngine.Game;

import KanapkaEngine.Components.vec2d;
import KanapkaEngine.Game.Time;
import org.joml.Vector2d;

public class Particle {
    private final vec2d position = new vec2d(0.0, 0.0);
    private final vec2d velocity = new vec2d(0.0, 0.0);

    private final double birth = Time.time();

    public Particle(Vector2d position) {
        this.position.set(position);
    }

    public final boolean isDead(double lifeTime) {
        return birth + lifeTime < Time.time();
    }

    public final Vector2d getVelocity() {
        return velocity;
    }

    public final void setVelocity(Vector2d velocity) {
        this.velocity.set(velocity);
    }

    public final Vector2d getPosition() {
        return position;
    }

    public final void setPosition(Vector2d position) {
        this.position.set(position);
    }

    public final void addPosition(Vector2d add) {
        position.set(position.add(add));
    }

    public final void addVelocity(Vector2d add) {
        velocity.set(velocity.add(add));
    }
}
