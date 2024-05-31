package KanapkaEngine.Components;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The IndexRenderer allows for allocation of images to a global list which instead can be accessed with ids greatly reducing memory allocation. This method avoids assigning the same texture for a lot of nodes, instead using a pointer to access the same texture. <br>
 * <br> The <strong><code>id</code></strong> is a public variable on the IndexRenderer instance.
 */
public class IndexRenderer extends Renderer {
    public int id = -1;

    private static final List<BufferedImage> BUFFERED_IMAGES = new ArrayList<>();

    /**
     *
     * @param image To be added to the global list.
     * @return The index of the added image, should be recalled somehow in order to assign a texture using this system.
     */
    public static int addImage(BufferedImage image) {
        BUFFERED_IMAGES.add(image);
        return BUFFERED_IMAGES.size() - 1;
    }

    private static BufferedImage getImage(int index) {
        if (index < 0 || index >= BUFFERED_IMAGES.size()) return null;
        return BUFFERED_IMAGES.get(index);
    }

    public BufferedImage getImage() {
        return getImage(id);
    }

    @Override
    public BufferedImage getRender() {
        return getImage();
    }
}
