package KanapkaEngine.Components;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.HashMap;

public class TextureAnimation {
    private int resolution;
    private BufferedImage spriteSheet;
    private HashMap<String, anim> animations = new HashMap<>();
    public TextureAnimation(String spriteSheetPath, int resolution) {
        this(ResourceLoader.loadResource(spriteSheetPath), resolution);
    }

    public TextureAnimation(BufferedImage spriteSheet, int resolution) {
        this.spriteSheet = spriteSheet;
        this.resolution = resolution;
    }

    public void defineAnim(int row, int length, String animationName) {
        if (!animations.containsKey(animationName))
            animations.put(animationName, new anim() {
                @Override
                public int len() {
                    return length;
                }

                @Override
                public int row() {
                    return row;
                }
            });
        else
            System.err.println("Animation already exists");
    }

    public BufferedImage getAnimationFrame(String name, int frame) {
        anim a = animations.get(name);
        if (a != null) {
            int w = (int)Math.floor((double) spriteSheet.getWidth() / resolution);
            int p = (frame % a.len()) % w;
            int r = a.row() + (int)Math.floor(frame / a.len());
            if (Math.floor((double) spriteSheet.getHeight() / resolution) <= r)
                return spriteSheet.getSubimage(p * resolution, r * resolution, resolution, resolution);
        }

        return null;
    }

    private interface anim {
        int len();
        int row();
    }
}
