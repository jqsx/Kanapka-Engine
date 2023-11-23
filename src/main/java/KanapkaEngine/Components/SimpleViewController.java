package KanapkaEngine.Components;

import KanapkaEngine.Engine;
import KanapkaEngine.Game.Plugin;
import KanapkaEngine.Time;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SimpleViewController extends Plugin implements MouseWheelListener {
    Vector2D velocity = new Vector2D(0, 0);
    @Override
    public void Apply(Engine engine) {
        engine.addListener(this);
    }

    @Override
    public void Update() {
        velocity = velocity.scalarMultiply(1.0 - Time.deltaTime());
        
    }

    @Override
    public void Detach() {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int s = (int) Math.signum(e.getPreciseWheelRotation());
        Point total = new Point(0, 0);
        if (e.isShiftDown())
            total.translate(s, 0);
        else
            total.translate(0, s);

        if (Camera.main != null) {
            velocity = velocity.add(new Vector2D(total.x, total.y));
        }
    }
}
