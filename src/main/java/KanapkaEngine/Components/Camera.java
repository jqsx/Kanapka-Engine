package KanapkaEngine.Components;

import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

public class Camera {
    public static Camera main = new Camera();

    private Vector2D position = new Vector2D(0, 0);
    private double rotation = 0f;
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
    public Vector2D ScreenToWorldPosition(Point screen_position) {
        Dimension screen = Window.getWindowSize();
        return position.subtract(new Vector2D(screen.getWidth() / 2.0, screen.getHeight() / 2.0)).add(new Vector2D(screen_position.x, -screen_position.y));
    }

    public Point WorldToScreenPosition(Vector2D world) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        Vector2D p = world.add(position).subtract(new Vector2D(screen.getWidth() / 2.0, screen.getHeight() / 2.0));

        return new Point((int)Math.round(p.getX()), (int)Math.round(p.getY()));
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
