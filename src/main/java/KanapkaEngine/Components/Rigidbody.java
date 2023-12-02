package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.world.World;

public class Rigidbody extends NodeComponent {
    private Body body;
    @Override
    void Start() {

    }

    @Override
    void Update() {
        Vector2 body_position = body.getWorldCenter();
        getParent().transform.setPosition(new Vector2D(body_position.x, body_position.y));
    }

    @Override
    void onParent() {
        Append();
    }

    @Override
    void onOrphan() {
        Detach();
    }

    @Override
    void onDestroy() {

    }

    @Override
    String toJSON() {
        return null;
    }

    final void constructBody() {
        Detach();
        body = new Body();
        Collider collider = getParent().getComponent(Collider.class);
//        body.setMassType(MassType.NORMAL);
        if (collider != null) {
            BodyFixture fixture = collider.getFixture();
            body.setMass(fixture.createMass());
            body.setMassType(MassType.FIXED_ANGULAR_VELOCITY);
            body.addFixture(fixture);
            body.updateMass();
            System.out.println("Found and added collider");
        }
    }

    public final void Append() {
        if (body == null)
            constructBody();
        Physics.addBody(body);
    }

    public final void Detach() {
        if (body != null)
            Physics.removeBody(body);
    }

    public Body getBody() {
        return body;
    }
}
