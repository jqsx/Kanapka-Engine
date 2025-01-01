package KanapkaEngine.Components;

import KanapkaEngine.Game.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector2d;

import java.awt.*;
import static org.lwjgl.glfw.GLFW.*;

public class SimpleViewController extends Plugin implements IInput {
    private vec2d velocity = new vec2d(0, 0);
    private Vector2d range = new Vector2d(0.001, 30.0);

    private final Vector2d move = new Vector2d();

    Point input = new Point(0, 0);

    @Override
    public void Apply(Engine engine) {

    }

    @Override
    public void Update() {
        if (Camera.main == null)
            return;

        velocity.set(velocity.mul(1.0 - Time.deltaTime() * 3.0));

        move.set((Input.isKeyDown(GLFW_KEY_A) ? -1.0 : 0.0) + (Input.isKeyDown(GLFW_KEY_D) ? 1.0 : 0.0), (Input.isKeyDown(GLFW_KEY_S) ? -1.0 : 0.0) + (Input.isKeyDown(GLFW_KEY_W) ? 1.0 : 0.0));

        velocity.set(velocity.add(move.mul(Time.deltaTime() * (Camera.main.size / 10.0) * 20.0)));

        Camera.main.setPosition(Camera.main.getPosition().add(velocity.mul(Time.deltaTime() * 5)));

        Camera.main.setRotation(Math.toRadians(Mathf.Clamp(velocity.x, -10.0, 10.0)));
    }

    @Override
    public void KeyCallBack(int key, int scancode, int action, int mods) {

    }

    @Override
    public void CharKeyCallback(int c) {

    }

    @Override
    public void ScrollCallback(double x, double y) {
        ImGuiIO io = ImGui.getIO();

        if (io.getWantCaptureMouse())
            return;

        if (!SceneManager.hasScene()) return;
        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL) || Input.isKeyDown(GLFW_KEY_RIGHT_CONTROL)) {
            Camera.main.size = (Mathf.Clamp(Camera.main.size - (y + x) / 10.0, range.x, range.y));
        }
        else {
            Vector2d total = new Vector2d(x, y);

            if (Camera.main != null) {
                velocity.set(velocity.add(total.mul(1.0 / Camera.main.size)));
            }
        }
    }
}
