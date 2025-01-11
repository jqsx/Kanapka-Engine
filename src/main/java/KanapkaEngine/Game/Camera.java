package KanapkaEngine.Game;

import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Components.vec2d;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The class responsible for the orientation of the player camera.
 */
public class Camera {
    /**
     * Main global camera that is used for rendering. Set this variable if you want to change cameras.
     */
    public static Camera main = new Camera();

    private final vec2d position = new vec2d(0, 0);
    private double rotation = 0f;
    public double size = 1;

    public float NEAR = 0.01f;
    public float FAR = 1000.f;
    public float FOV = 60.f;

    private Matrix4f projectionMatrix = new Matrix4f();

    public Projection projection = Projection.ORTHOGRAPHIC;

    private final Rectangle2D bounds = new Rectangle2D.Double();

    public Camera() {
        if (main == null) {
            main = this;
        }
    }

    private void refreshBounds() {
        Window window = Engine.getMainInstance().getWindow();

        float ratio = window.getWidth() / (float)window.getHeight();

        float reverseRatio = 1.f / ratio;

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 20.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 20.0);

        float width = (float)size * widthMult;
        float height = (float)size * heightMult;

        bounds.setRect(getPosition().x - width, getPosition().y - height, width * 2, height * 2);
    }

    public boolean isWithin(Renderer renderer) {
        return bounds.intersects(renderer.bounds());
    }

    public Vector2d getPosition() {
        return position.clone();
    }

    /**
     * Shitty fix because camera coordinates are inverted idk this should work in world coordinates now tho
     * @return
     */
    public Vector2d getWorldPosition() {
        return position.mul(-1);
    }

    public void setPosition(Vector2d position) {
        this.position.set(position);
    }

    public void setPosition(double x, double y) {
        this.position.set(x,y);
    }

    public Vector2d ScreenToWorldPosition(Point screen_position) {

        Window window = Engine.getMainInstance().getWindow();

        float ratio = window.getWidth() / (float)window.getHeight();

        float reverseRatio = 1.f / ratio;

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 20.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 20.0);
        return new Vector2d((screen_position.x / (double)window.getWidth() * 2 - 1) * Camera.main.size * widthMult, (-screen_position.y / (double)window.getHeight() * 2 + 1) * Camera.main.size * heightMult).add(Camera.main.getPosition());
    }

    public Point WorldToScreenPosition(Vector2d world) {
        Vector2d camRelative = world.sub(Camera.main.getPosition());

        Window window = Engine.getMainInstance().getWindow();

        float ratio = window.getWidth() / (float)window.getHeight();

        float reverseRatio = 1.f / ratio;

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 20.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 20.0);

        camRelative.x /= (Camera.main.size * widthMult);
        camRelative.y /= (Camera.main.size * heightMult);

        camRelative.x += 1;
        camRelative.y -= 1;

        camRelative.x *= window.getWidth();
        camRelative.y *= window.getHeight();

        return new Point((int) camRelative.x, (int) camRelative.y);
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

        float heightMult = (float) Mathf.Clamp(reverseRatio, 1.0, 20.0);
        float widthMult = (float) Mathf.Clamp(ratio, 1.0, 20.0);

        if (main.projection == Projection.ORTHOGRAPHIC)
            proj = proj.identity().ortho2D(-size * widthMult, size * widthMult, -size * heightMult, size * heightMult);
        else if (main.projection == Projection.PERSPECTIVE)
            proj = proj.identity().perspective((float)Math.toRadians(main.FOV), ratio, main.NEAR, main.FAR);

        proj.rotateZ((float)Camera.main.rotation);

        Camera.main.projectionMatrix = proj;

        Camera.main.refreshBounds();
    }

    public enum Projection {
        ORTHOGRAPHIC, PERSPECTIVE
    }
}
