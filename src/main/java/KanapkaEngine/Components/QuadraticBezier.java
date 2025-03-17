package KanapkaEngine.Components;

import org.joml.Vector2d;

public class QuadraticBezier {
    public final Vector2d A = new Vector2d(0, 1);

    public QuadraticBezier() {

    }

    public void run(Vector2d from, Vector2d to, Vector2d out, double t) {
        t = Mathd.Clamp01(t);

        double dt = Math.pow(t, 2);
        double dti = Math.pow(1.0 - t, 2);

        double x = from.x * dti + 2 * dt * dti * A.x + dt * to.x;
        double y = from.y * dti + 2 * dt * dti * A.y + dt * to.y;

        out.set(x,y);
    }
}
