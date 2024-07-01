package KanapkaEngine.UI;

import KanapkaEngine.Components.ResourceLoader;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

public class Image extends UIComponent {

    private BufferedImage render;

    @Override
    public void render(Graphics2D main, AffineTransform at) {
        if (render != null) {
            /*AffineTransform ata = new AffineTransform();
            Dimension windowSize = Window.getWindowSize();
            float x = (float)(position.getX() - render.getWidth() / 2.0 + (pivot.getX() >= 0 ? windowSize.width / (2.0 - pivot.getX()) : 0));
            float y = (float)(position.getY() - render.getHeight() / 2.0 + (pivot.getY() >= 0 ? windowSize.height / (2.0 - pivot.getY()) : 0));
            ata.translate(x, y);
            main.drawImage(render, at, null);*/

            drawImage(main, render, pivot, new Point((int) position.getX(), (int) position.getY()));
        }
    }

    private void drawImage(Graphics2D g, BufferedImage image, Pivot pivot, Point offset) {
        Dimension screen = Window.getWindowSize();
        Dimension dImage = new Dimension(image.getWidth(), image.getHeight());
        Vector2D scaledDimensions = new Vector2D(size.getX() / dImage.width, size.getY() / dImage.height);
        Vector2D relative_offset = new Vector2D((size.getX() / 2.0) * (1 + pivot.getX()) , (size.getY() / 2.0) * (1 + pivot.getY()));
        Point move = new Point((int) (screen.width + pivot.getX() * screen.width + offset.x - relative_offset.getX()),
                (int) (screen.height + pivot.getY() * screen.height + offset.y - relative_offset.getY()));

        AffineTransform at = new AffineTransform();

        at.scale(scaledDimensions.getX(), scaledDimensions.getY());

        at.translate(move.x / scaledDimensions.getX(), move.y / scaledDimensions.getY());

        g.drawImage(image, at, null);
    }

    private Rectangle2D drawText(Graphics2D g, String text, int color, int color2, Pivot pivot, Point offset) {
        Dimension screen = Window.getWindowSize();
        Point move = new Point(screen.width + pivot.getX() * screen.width + offset.x, screen.height + pivot.getY() * screen.height + offset.y);

        Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

        bounds.setRect(move.x - bounds.getWidth() / 2.0 * (1 + pivot.getX()), move.y - bounds.getHeight() / 2.0 * (1 + pivot.getY()), bounds.getWidth(), bounds.getHeight());

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

    public final void setImage(BufferedImage image) {
        this.render = image;
    }

    public final void setImage(String path) {
        this.render = ResourceLoader.loadResource(path);
    }
}
