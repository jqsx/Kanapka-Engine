package KanapkaEngine.Components;

import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SimpleViewController extends Plugin implements MouseWheelListener {
    Vector2D velocity = new Vector2D(0, 0);
    Vector2D range = new Vector2D(0.001, 30.0);
    @Override
    public void Apply(Engine engine) {
        engine.addListener(this);
    }

    @Override
    public void Update() {
        velocity = velocity.scalarMultiply(1.0 - Time.deltaTime() * 3.0);
        if (Camera.main != null) {
            Camera.main.setPosition(Camera.main.getPosition().add(velocity.scalarMultiply(Time.deltaTime() * 5)));
        }
    }

    @Override
    public void Detach() {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double s = e.getPreciseWheelRotation();
        if (!SceneManager.hasScene()) return;
        if (e.isControlDown()) {
            Scene scene = SceneManager.getCurrentlyLoaded();
            scene.setGlobalSize(Mathf.Clamp(scene.getGlobalSize() - s / 10.0, range.getX(), range.getY()));
        }
        else {
            Vector2D total = e.isShiftDown() ? new Vector2D(-s, 0) : new Vector2D(0, s);

            if (Camera.main != null) {
                velocity = velocity.add(total.scalarMultiply(1.0 / SceneManager.getCurrentlyLoaded().getGlobalSize()));
            }
        }
    }
}
