package KanapkaEngine.Components;

import KanapkaEngine.Game.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ResourceLoader {
    private static final Logger logger = new Logger("ResourceLoader");

    /**
     * Load resources from the project's resources folder.
     * @param path
     * @return The loaded resource as BufferedImage
     */
    public static BufferedImage loadImageResource(String path) {
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

    /**
     *
     * @param path
     * @return An InputStream of file information from the resources folder of the project.
     */
    public static InputStream loadStreamResource(String path) {
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

    public static AudioClip loadAudioResoource(String path) {
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

    public static String loadStringFromResource(String path) {
        try {
            InputStream stream = loadStreamResource(path);

            if (stream == null)
                return null;

            StringBuilder builder = new StringBuilder();

            int value;
            while((value = stream.read()) != -1) {
                char c = (char) value;

                builder.append(c);
            }

            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
