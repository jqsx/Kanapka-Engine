package KanapkaEngine.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public final class Texture {
    static final List<Texture> LoadedTextures = new ArrayList<>();

    int textureId;

    int width;
    int height;

    boolean isDisposed = false;

    public Texture(BufferedImage image) {
        Objects.requireNonNull(image);

        textureId = glGenTextures();

        Engine.ErrorCheck("Gen tex " + textureId);

        setTexture(image);

        LoadedTextures.add(this);
    }

    public void setTexture(BufferedImage image) {
        if (isDisposed)
            return;

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        Engine.ErrorCheck("bind tex");

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

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

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        Engine.ErrorCheck("Buffer");
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
}
