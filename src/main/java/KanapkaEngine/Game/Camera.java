package KanapkaEngine.Game;

import KanapkaEngine.Components.Mathf;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import java.awt.*;

/**
 * The class responsible for the orientation of the player camera.
 */
public class Camera {
    /**
     * Main global camera that is used for rendering. Set this variable if you want to change cameras.
     */
    public static Camera main = new Camera();

    private Vector2d position = new Vector2d(0, 0);
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

    public Vector2d getPosition() {
        return position;
    }

    /**
     * Shitty fix because camera coordinates are inverted idk this should work in world coordinates now tho
     * @return
     */
    public Vector2d getWorldPosition() {
        return position.mul(-1);
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public Vector2d ScreenToWorldPosition(Point screen_position) {
//        Dimension screen = Window.getWindowSize();
//        return position.subtract(new Vector2d(screen.getWidth() / 2.0, screen.getHeight() / 2.0)).add(new Vector2d(screen_position.x, -screen_position.y));
        return null;
    }

    public Point WorldToScreenPosition(Vector2d world) {

        double gSize = SceneManager.getGlobalSize();

        Vector2d cameraPosition = Camera.main.getPosition();
        Vector2d position = cameraPosition.add(world);

        return new Point((int) (position.x * gSize), (int) (-position.x * gSize));
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
