package KanapkaEngine.UI;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent {
    private List<UIComponent> children = new ArrayList<>();
    private UIComponent parent;
    public Point pivot = new Point(0, 0);
    public Vector2D position = Vector2D.ZERO;
    public Vector2D size = new Vector2D(100, 100);

    public final UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }

    public final void Render(Graphics2D main) {
        render(main);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).Render(main);
        }
    }

    public abstract void render(Graphics2D main);
}
