package KanapkaEngine.UI;

import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIComponent {
    private List<UIComponent> children = new ArrayList<>();
    private UIComponent parent;
    public Pivot pivot = Pivot.Center;
    public Vector2D origin = new Vector2D(0.5, 0.5);
    public Vector2D position = Vector2D.ZERO;
    public Vector2D size = new Vector2D(100, 100);

    public final UIComponent getParent() {
        return parent;
    }

    public UIComponent() {
        setParent(UI.currentlyDisplayed);
    }

    public void setParent(UIComponent parent) {
        Objects.requireNonNull(parent);
        if (this.parent != null)
            this.parent.children.remove(this);
        this.parent = parent;
        parent.children.add(this);
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
        float x = (float)(position.getX() + (pivot.getX() >= 0 ? windowSize.width / (2.0 - pivot.getX()) : 0));
        float y = (float)(position.getY() + (pivot.getY() >= 0 ? windowSize.height / (2.0 - pivot.getY()) : 0));
        at.translate(x, y);
        //at.translate(origin.getX() * size.getX(), origin.getY() * size.getY());
        return at;
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
