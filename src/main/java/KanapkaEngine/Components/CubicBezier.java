package KanapkaEngine.Components;

import org.joml.Vector2d;

public class CubicBezier {
    public final Vector2d A = new Vector2d(0.25, 1);
    public final Vector2d B = new Vector2d(0.75, -1);

    public CubicBezier() {

    }

    public void run(Vector2d from, Vector2d to, Vector2d out, double t) {
        t = Mathf.Clamp01(t);

        double dt = Math.pow(t, 3);
        double dti = Math.pow(1.0 - t, 3);

        double x = from.x * dti + 2 * dti * t * A.x + 2 * (1 - t) * dt * B.x + dt * to.x;
        double y = from.y * dti + 2 * dti * t * A.y + 2 * (1 - t) * dt * B.x + dt * to.y;

        out.set(x,y);
    }
}
