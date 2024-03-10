package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ResourceLoader {
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

    public static InputStream loadStream(String path) {
        return ClassLoader.getSystemResourceAsStream(path);
    }

    public static BufferedImage loadFile(String path) {
        return null;
    }
    private ResourceLoader() {}

    public static AudioClip loadAudio(String path) {
        try {
            Clip clip = AudioSystem.getClip();

            InputStream audioSrc = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip.open(audioStream);

            return new AudioClip(clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
