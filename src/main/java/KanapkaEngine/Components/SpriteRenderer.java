package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SpriteRenderer extends Renderer {
    /**
     * 0 - not started
     * 1 - started
     * 2 - finished
     */
    private String texture_path;

    @Override
    public BufferedImage getRender() {
        if (loading_state == NOT_STARTED) LoadTexture();
        else if (loading_state == STARTED) return rendered_visual == null ? null : rendered_visual;
        else if (loading_state == FINISHED) return rendered_visual;

        return null;
    }

    private void LoadTexture() {
        if (texture_path == null) return;
        loading_state = STARTED;
        new Thread(() -> {
            BufferedImage _texture = ResourceLoader.loadResource(texture_path);
            if (_texture == null) {
                createErrorTexture();
            }
            else {
                rendered_visual = _texture;
            }

            loading_state = FINISHED;
        }).start();
    }

    private void createErrorTexture() {
        Vector2D size = getParent().transform.getSize();
        rendered_visual = new BufferedImage((int) (size.getX() * 16.0), (int) (size.getY() * 16.0), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rendered_visual.createGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, (int) (size.getX() * 16.0), (int) (size.getY() * 16.0));
        g.setColor(Color.black);
        g.setFont(g.getFont().deriveFont((float) (size.getY() * 16.0)));
        g.drawString("Err", 0, (int) (size.getY() * 16.0));

        g.dispose();
    }

    public void setTexture(String path) {
        loading_state = NOT_STARTED;
        texture_path = path;
    }

    public int getProgress() {
        return loading_state;
    }
}
