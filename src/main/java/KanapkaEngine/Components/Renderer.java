package KanapkaEngine.Components;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Renderer extends NodeComponent {
    BufferedImage rendered_visual;
    int loading_state = NOT_STARTED;
    public abstract BufferedImage getRender();

    public void setTexture(BufferedImage image) {
        rendered_visual = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        loading_state = FINISHED;
        Graphics2D g = rendered_visual.createGraphics();
        Dimension size = new Dimension(rendered_visual.getWidth(), rendered_visual.getHeight());
        AffineTransform at = new AffineTransform();
        at.translate(size.width / 2.0, size.height / 2.0);
        at.scale(1, -1);
        at.translate(-size.width / 2.0, -size.height / 2.0);
        g.drawImage(image, at, null);

        g.dispose();
    }
    public final static int NOT_STARTED = 0;
    public final static int STARTED = 1;
    public final static int FINISHED = 2;
}
