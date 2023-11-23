package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TextureLoader {
    public static BufferedImage loadResource(String path) {
        InputStream stream = TextureLoader.class.getResourceAsStream(path);
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
    private TextureLoader() {}
}
