package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Camera {
    public static Camera main = new Camera();

    private Vector2D position = new Vector2D(0, 0);
    public double size = 1;
    public Camera() {
        if (main == null) {
            main = this;
        }
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public static void setMainCamera(Camera camera) {
        main = camera;
    }
}
