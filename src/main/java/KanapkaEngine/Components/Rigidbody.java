package KanapkaEngine.Components;

import org.joml.Vector2d;

public class Rigidbody extends Component {
    private Vector2d velocity = new Vector2d(0,0);

    private double Mass = 1.0;

    private double Bounce = 0.3;

    public Vector2d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity = velocity;
    }

    public Vector2d addVelocity(Vector2d force) {
        return velocity = velocity.add(force);
    }

    public double getMass() {
        return Mass;
    }

    public void setMass(double mass) {
        Mass = Math.abs(mass);
    }

    public double getBounce() {
        return Bounce;
    }

    public void setBounce(double bounce) {
        Bounce = Math.abs(bounce);
    }
}
