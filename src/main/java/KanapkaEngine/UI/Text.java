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

    private BufferedImage render;
    private FontRenderContext context;

    private void generateVisual() {
        if (context == null) return;
        Rectangle2D rect = font.deriveFont(size).getStringBounds(text, context);
        BufferedImage image = new BufferedImage((int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.drawString(text, 0, size);
        g.dispose();
        Vector2D scaled_size = super.size.scalarMultiply(0.01);
        render = new BufferedImage((int)Math.round(image.getWidth() / scaled_size.getX()), (int)Math.round(image.getHeight() / scaled_size.getY()), BufferedImage.TYPE_INT_ARGB);
        g = render.createGraphics();
        AffineTransform at = new AffineTransform();
        at.scale(1.0 / scaled_size.getX(), 1.0 /scaled_size.getY());
        g.drawImage(image, at, null);
        g.dispose();
    }
    @Override
    public void render(Graphics2D main, AffineTransform at) {
        if (context == null) {
            context = main.getFontRenderContext();
            generateVisual();
        }
        main.drawImage(render, at, null);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        generateVisual();
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
        generateVisual();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;

        generateVisual();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;

        generateVisual();
    }
}
