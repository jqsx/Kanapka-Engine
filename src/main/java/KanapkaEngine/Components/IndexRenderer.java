package KanapkaEngine.Components;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class IndexRenderer extends Renderer {
    public int id = -1;

    private static final List<BufferedImage> BUFFERED_IMAGES = new ArrayList<>();

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
