package KanapkaEngine.Components;

import KanapkaEngine.Time;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

public class Physics {
    private static final World<Body> world = getWorld();

    /**
     * Please don't mess with this function it should only be called by the Engine class.
     */
    public static void UpdateWorld() {
        world.update(Time.deltaTime());
    }

    public static void addBody(Body body) {
        world.addBody(body);
    }

    public static void removeBody(Body body) {
        world.removeBody(body);
    }

    public static void setGravity(double x, double y) {
        world.setGravity(x, y);
    }

    private static World<Body> getWorld() {
        World<Body> _world = new World<>();
        _world.setBounds(new AxisAlignedBounds(Double.MIN_VALUE, Double.MAX_VALUE));
        return _world;
    }
}
