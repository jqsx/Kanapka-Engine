package KanapkaEngine.Components;

import KanapkaEngine.Game.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureAtlas {
    private final Texture textures;

    private final HashMap<String, BufferedImage> subTextures = new HashMap<>();

    public TextureAtlas(Texture textures) {
        this.textures = textures;
    }
}
