package KanapkaEngine.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.glGetTextureImage;
import static org.lwjgl.opengl.GL46.glClearTexImage;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public final class Texture {
    static final List<Texture> LoadedTextures = new ArrayList<>();

    private static final Logger logger = new Logger("texture");

    int textureId;

    int width;
    int height;

    private boolean hasTexture = false;

    boolean isDisposed = false;

    public Texture() {
        if (!Engine.isOpenGLInitialized()) {
            throw new RuntimeException("CANNOT INSTANTIATE OBJECTS BEFORE INITIALIZING THE ENGINE.");
        }
        generateTextureID();

        LoadedTextures.add(this);
    }

    public Texture(Texture texture) {
        this();

        setTexture(texture);
    }

    private void generateTextureID() {
        textureId = glGenTextures();

        Engine.ErrorCheck("Gen tex " + textureId);
    }

    public Texture(BufferedImage image) {
        this();

        setTexture(image);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setTexture(BufferedImage image) {
        if (isDisposed)
            return;

        if (image == null) {
            image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
        }

        hasTexture = true;

        width = image.getWidth();
        height = image.getHeight();

        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, height - y - 1), true);
                buffer.put((byte)color.getRed());
                buffer.put((byte)color.getGreen());
                buffer.put((byte)color.getBlue());
                buffer.put((byte)color.getAlpha());
            }
        }

        buffer.flip();

        setTexture(width, height, buffer);
    }

    public void setTexture(int width, int height, int[] data) {

        if (data.length != width * height * 4) {
            logger.error("Failed to buffer texture: int data array doesn't contain enough/contains too many elements for the given texture resolution.");
            return;
        }

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Set texture without checks if the data is correct
     * @param width
     * @param height
     * @param data
     */
    public void setTextureUnsafe(int width, int height, int[] data) {
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTexture(int width, int height, ByteBuffer data) {
        if (data.capacity() != width * height * 4) {
            logger.error("Failed to buffer texture: int data array doesn't contain enough/contains too many elements for the given texture resolution.");
            return;
        }

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTexture(Texture texture) {
        Objects.requireNonNull(texture);

        if (!texture.hasTexture)
            return;

        int[] pixels = new int[texture.width * texture.height * 4];

        glBindTexture(GL_TEXTURE_2D, texture.textureId);
        glGetTextureImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

        setTexture(texture.width, texture.height, pixels);
    }

    public void Dispose() {
        if (isDisposed)
            return;
        Dispose(true);
    }

    void Dispose(boolean removeFromList) {
        glDeleteTextures(textureId);

        if (removeFromList)
            LoadedTextures.remove(this);
    }

    public void flush() {
        setTextureUnsafe(0, 0, null);
    }

    public boolean HasTexture() {
        return hasTexture;
    }
}
