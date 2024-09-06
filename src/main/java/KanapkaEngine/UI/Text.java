package KanapkaEngine.UI;

import KanapkaEngine.Engine;
import KanapkaEngine.Game.Input;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Text extends UIComponent {
    private String text = "";
    private float size = 10f;
    private int color = 0xffffff;
    private int background = 0x000000;
    private Font font;

    private Rectangle2D bounds;

    public Text() {

    }

    @Override
    public void render(Graphics2D main, AffineTransform at) {
        if (font != null)
            main.setFont(font.deriveFont(size));
        else
            main.setFont(main.getFont().deriveFont(size));

        Vector2D p = getTPosition();

        //bounds != null ? bounds.toString() + "\n" + Input.getMousePosition().toString() : "Bonkers"
        bounds = drawText(main, text, color, background, pivot, new Point((int) p.getX(), (int) p.getY()));
    }

    private Rectangle2D drawText(Graphics2D g, String text, int color, int color2, Pivot pivot, Point offset) {
        Dimension screen = Window.getWindowSize();
        Point move = new Point(screen.width + pivot.getX() * screen.width + offset.x, screen.height + pivot.getY() * screen.height + offset.y);

        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

        bounds.setRect(move.x - (bounds.getWidth() / 2.0) * (1 + pivot.getX()), move.y - (bounds.getHeight() / 2.0) * (1 + pivot.getY()), bounds.getWidth(), bounds.getHeight());

        Color c = new Color(color);
        Color darker = new Color(color2);

        g.setColor(darker);
        g.fillRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
        g.setColor(darker.brighter());
        g.setStroke(new BasicStroke(3f));
        g.drawRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
        g.setColor(c);
        g.drawString(text, (int) bounds.getX(), (int) (bounds.getY() + g.getFont().getSize()));

        Vector2D mouse = Input.getMousePosition();

        if (bounds.contains(mouse.getX(), mouse.getY())) {
            g.setColor(new Color(255, 255, 255, 150));
        }
        else {
            g.setColor(new Color(255, 255, 255, 50));
        }

        g.fillRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());

        return bounds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }
}
