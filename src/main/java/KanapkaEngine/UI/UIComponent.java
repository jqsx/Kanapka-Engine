package KanapkaEngine.UI;

import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent {
    private List<UIComponent> children = new ArrayList<>();
    private UIComponent parent;
    public Point pivot = new Point(0, 0);
    public Vector2D origin = new Vector2D(0.5, 0.5);
    public Vector2D position = Vector2D.ZERO;
    public Vector2D size = new Vector2D(100, 100);

    public final UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }

    public final void Render(Graphics2D main) {
        render(main, getTransformation());
        for (int i = 0; i < children.size(); i++) {
            children.get(i).Render(main);
        }
    }

    private AffineTransform getTransformation() {
        AffineTransform at = new AffineTransform();
        Dimension windowSize = Window.getWindowSize();
        at.translate(windowSize.width * (1 + Math.signum(pivot.x)), windowSize.height * (1 + Math.signum(pivot.y)));
        at.translate(position.getX(), position.getY());
        at.translate(origin.getX() * size.getX(), origin.getY() * size.getY());
        return at;
    }

    public abstract void render(Graphics2D main, AffineTransform at);
}
