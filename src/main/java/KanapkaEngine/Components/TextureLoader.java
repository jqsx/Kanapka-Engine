package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class TextureLoader {
    private static HashMap<String, BufferedImage> loadedImages = new HashMap<>();
    public static BufferedImage loadResource(String path) {
        if (loadedImages.containsKey(path)) return loadedImages.get(path);
        InputStream stream = ClassLoader.getSystemResourceAsStream(path);
        if (stream != null) {
            try {
                BufferedImage image = ImageIO.read(stream);
                loadedImages.put(path, image);
                return image;
            } catch (IOException e) {
                System.out.println(e);
                return null;
            }
        }
        return null;
    }

    public static BufferedImage loadFile(String path) {
        return null;
    }
    private TextureLoader() {}
}
