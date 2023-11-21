package KanapkaEngine.Components;

import java.awt.image.BufferedImage;

public abstract class Renderer extends NodeComponent {
    BufferedImage rendered_visual;
    public abstract BufferedImage getRender();

    public void setTexture(BufferedImage image) {

    }

    public void setTexture(String image) {

    }
}
