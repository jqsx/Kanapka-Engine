package KanapkaEngine.Game;

import KanapkaEngine.Components.Mathf;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.joml.Matrix4f;

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

    public float NEAR = 0.01f;
    public float FAR = 1000.f;
    public float FOV = 60.f;

    private Matrix4f projectionMatrix = new Matrix4f();

    public Projection projection = Projection.ORTHOGRAPHIC;

    public Camera() {
        if (main == null) {
            main = this;
        }
    }

    public Vector2D getPosition() {
        return position;
    }

    /**
     * Shitty fix because camera coordinates are inverted idk this should work in world coordinates now tho
     * @return
     */
    public Vector2D getWorldPosition() {
        return position.scalarMultiply(-1);
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D ScreenToWorldPosition(Point screen_position) {
//        Dimension screen = Window.getWindowSize();
//        return position.subtract(new Vector2D(screen.getWidth() / 2.0, screen.getHeight() / 2.0)).add(new Vector2D(screen_position.x, -screen_position.y));
        return null;
    }

    public Point WorldToScreenPosition(Vector2D world) {

        double gSize = SceneManager.getGlobalSize();

        Vector2D cameraPosition = Camera.main.getPosition();
        Vector2D position = cameraPosition.add(world);

        return new Point((int) (position.getX() * gSize), (int) (-position.getY() * gSize));
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public static Matrix4f getProjectionMatrix() {
        if (Camera.main == null)
            return null;

        return Camera.main.projectionMatrix;
    }

    static void createProjectionMatrix(float ratio) {
        if (Camera.main == null)
            return;

        Matrix4f proj = Camera.main.projectionMatrix;
        float size = (float) Camera.main.size;

        float reverseRatio = 1.f / ratio;

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 2.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 2.0);

        if (main.projection == Projection.ORTHOGRAPHIC)
            proj = proj.identity().ortho2D(-size * widthMult, size * widthMult, -size * heightMult, size * heightMult);
        else if (main.projection == Projection.PERSPECTIVE)
            proj = proj.identity().perspective((float)Math.toRadians(main.FOV), ratio, main.NEAR, main.FAR);

        Camera.main.projectionMatrix = proj;
    }

    public enum Projection {
        ORTHOGRAPHIC, PERSPECTIVE
    }
}
