package KanapkaEngine.UI;

import KanapkaEngine.Engine;
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
    private Color color = Color.white;
    private Font font;

    private Rectangle2D bounds;

    @Override
    public void render(Graphics2D main, AffineTransform at) {
        if (font != null)
            main.setFont(font.deriveFont(size));
        else
            main.setFont(main.getFont().deriveFont(size));
        bounds = drawText(main, text, color.getRGB(), 0x000000, pivot, new Point((int) position.getX(), (int) position.getY()));
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
