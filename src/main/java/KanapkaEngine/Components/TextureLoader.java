package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TextureLoader {
    public static BufferedImage loadResource(String path) {
        InputStream stream = ClassLoader.getSystemResourceAsStream(path);
        if (stream != null) {
            try {
                return ImageIO.read(stream);
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
