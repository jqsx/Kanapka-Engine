package KanapkaEngine.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureAtlas {
    private final BufferedImage textures;

    private final HashMap<String, BufferedImage> subTextures = new HashMap<>();

    public TextureAtlas(BufferedImage textures) {
        this.textures = textures;
    }

    public final void createSubTexture(String name, Rectangle rect) {
        subTextures.put(name, textures.getSubimage(rect.x, rect.y, rect.width, rect.height));
    }

    public final BufferedImage getSubTexture(String name) {
        return subTextures.get(name);
    }
}
