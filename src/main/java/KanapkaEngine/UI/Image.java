package KanapkaEngine.UI;

import KanapkaEngine.Components.ResourceLoader;
import KanapkaEngine.Game.Window;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Image extends UIComponent {

    private BufferedImage render;

    @Override
    public void render(Graphics2D main, AffineTransform at) {
        if (render != null) {
            AffineTransform ata = new AffineTransform();
            Dimension windowSize = Window.getWindowSize();
            float x = (float)(position.getX() - render.getWidth() / 2.0 + (pivot.getX() >= 0 ? windowSize.width / (2.0 - pivot.getX()) : 0));
            float y = (float)(position.getY() - render.getHeight() / 2.0 + (pivot.getY() >= 0 ? windowSize.height / (2.0 - pivot.getY()) : 0));
            ata.translate(x, y);
            main.drawImage(render, at, null);
        }
    }

    public final void setImage(BufferedImage image) {
        this.render = image;
    }

    public final void setImage(String path) {
        this.render = ResourceLoader.loadResource(path);
    }
}
