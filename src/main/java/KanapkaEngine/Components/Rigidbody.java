package KanapkaEngine.Components;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.world.World;

public class Rigidbody extends NodeComponent {
    private Body body;
    @Override
    void Start() {

    }

    @Override
    void Update() {

    }

    @Override
    void onDestroy() {

    }

    final void constructBody() {
        body = new Body();
        
    }

    public final void Append() {
        if (body == null)
            constructBody();
        Physics.world.addBody(body);
    }

    public final void Detach() {
        if (body != null)
            Physics.world.removeBody(body);
    }
}
