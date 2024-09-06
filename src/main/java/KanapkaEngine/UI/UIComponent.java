package KanapkaEngine.UI;

import KanapkaEngine.Game.Input;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

public class UIComponent {
    private List<UIComponent> children = new ArrayList<>();
    private UIComponent parent;
    public Pivot pivot = Pivot.Center;
    public Vector2D origin = new Vector2D(0.5, 0.5);
    public Vector2D position = Vector2D.ZERO;
    public Vector2D size = new Vector2D(100, 100);

    private final List<Runnable> onClick = new ArrayList<>();

    public final UIComponent getParent() {
        return parent;
    }

    public UIComponent() {
        setParent(UI.currentlyDisplayed);
    }

    public void setParent(UIComponent parent) {
        Objects.requireNonNull(parent);
        try {
            if (this.parent != null)
                this.parent.children.remove(this);
            this.parent = parent;
            parent.children.add(this);
        } catch (ConcurrentModificationException e) {
            setParent(parent);
        }
    }

    public final void Render(Graphics2D main) {
        render(main, getTransformation());
        for (UIComponent child : children) {
            child.Render(main);
        }
    }

    public Vector2D getTPosition() {
        if (parent != null)
            return getParent().getTPosition().add(position);
        else return position;
    }

    public final void addClickListener(Runnable r) {
        if (!onClick.contains(r))
            onClick.add(r);
    }

    public final void removeClickListener(Runnable r) {
        onClick.remove(r);
    }

    private void onClick() {
        Rectangle2D bounds = new Rectangle2D.Double();
        Vector2D mouse = Input.getMousePosition();
        if (bounds.contains(mouse.getX(), mouse.getY())) {
            for (Runnable r : onClick)
                r.run();
        }
    }

    private AffineTransform getTransformation() {
        AffineTransform at = new AffineTransform();
        Dimension windowSize = Window.getWindowSize();
        float x = (float)(position.getX() + (pivot.getX() >= 0 ? windowSize.width / (2.0 - pivot.getX()) : 0));
        float y = (float)(position.getY() + (pivot.getY() >= 0 ? windowSize.height / (2.0 - pivot.getY()) : 0));
        at.translate(x, y);
        //at.translate(origin.getX() * size.getX(), origin.getY() * size.getY());
        return at;
    }

    protected void buttonClickUpdate() {
        onClick();
        for (UIComponent component : children)
            component.buttonClickUpdate();
    }

    public void render(Graphics2D main, AffineTransform at) {

    }

    public enum Pivot {
        Top(0, -1), Center(0, 0), Right(1, 0), Left(-1, 0), Bottom(0, 1), TopLeft(-1, -1), TopRight(1, -1), BottomLeft(-1, 1), BottomRight(1, 1);

        private final Point pivot;

        Pivot(int x, int y) {
            this.pivot = new Point(x, y);
        }

        public int getX() {
            return pivot.x;
        }

        public int getY() {
            return pivot.y;
        }
    }
}
