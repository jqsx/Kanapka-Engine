package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class TextureLoader {
    public static BufferedImage loadResource(String path) {
        InputStream stream = ClassLoader.getSystemResourceAsStream(path);
        if (stream != null)
            return ImageIO.read(stream);
    }
    private TextureLoader() {}
}
