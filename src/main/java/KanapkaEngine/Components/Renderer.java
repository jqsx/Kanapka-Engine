package KanapkaEngine.Components;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Renderer extends NodeComponent {
    BufferedImage rendered_visual;
    int loading_state = NOT_STARTED;
    public abstract BufferedImage getRender();

    public void setTexture(BufferedImage image) {
        rendered_visual = image;
    }
    public final static int NOT_STARTED = 0;
    public final static int STARTED = 1;
    public final static int FINISHED = 2;
}
