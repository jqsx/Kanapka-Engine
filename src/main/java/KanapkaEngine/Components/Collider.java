package KanapkaEngine.Components;

import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

public abstract class Collider extends NodeComponent {
    public abstract BodyFixture getFixture();
}
