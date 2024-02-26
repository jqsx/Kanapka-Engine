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
    private Color color;

    private Font font;

    @Override
    public void render(Graphics2D main, AffineTransform at) {
        Dimension windowSize = Window.getWindowSize();
        main.setFont(main.getFont().deriveFont(size));
        main.setColor(color);
        float x = (float)(position.getX() + (pivot.getX() >= 0 ? windowSize.width / (2.0 - pivot.getX()) : 0));
        float y = (float)(position.getY() + (pivot.getY() >= 0 ? windowSize.height / (2.0 - pivot.getY()) : 0));
        main.drawString(text, (int)x, (int)y);
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
