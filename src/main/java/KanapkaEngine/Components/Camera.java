package KanapkaEngine.Components;

import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

/**
 * The class responsible for the orientation of the player camera.
 */
public class Camera {
    /**
     * Main global camera that is used for rendering. Set this variable if you want to change cameras.
     */
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

    public Vector2D ScreenToWorldPosition(Point screen_position) {
        Dimension screen = Window.getWindowSize();
        return position.subtract(new Vector2D(screen.getWidth() / 2.0, screen.getHeight() / 2.0)).add(new Vector2D(screen_position.x, -screen_position.y));
    }

    public Point WorldToScreenPosition(Vector2D world) {

        double gSize = SceneManager.getGlobalSize();

        Vector2D cameraPosition = Camera.main.getPosition();
        Vector2D position = new Vector2D(Math.round(cameraPosition.getX() * gSize) / gSize, Math.round(cameraPosition.getY() * gSize) / gSize).add(world);

        return new Point((int) position.getX(), (int) position.getY());
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
