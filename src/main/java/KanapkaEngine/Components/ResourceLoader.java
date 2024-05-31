package KanapkaEngine.Components;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

public class ResourceLoader {
    private static HashMap<String, BufferedImage> loadedImages = new HashMap<>();

    /**
     * Load resources from the project's resources folder.
     * @param path
     * @return The loaded resource as BufferedImage
     */
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

    /**
     *
     * @param path
     * @return An InputStream of file information from the resources folder of the project.
     */
    public static InputStream loadStream(String path) {
        return ClassLoader.getSystemResourceAsStream(path);
    }

    /**
     *
     * @param path
     * @return A file loaded from persistent storage.
     */
    public static InputStream loadFile(String path) {
        File file = new File(path);
        if (file.exists())
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return null;
            }
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
