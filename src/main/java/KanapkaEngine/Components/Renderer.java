package KanapkaEngine.Components;

import KanapkaEngine.Game.Texture;

import java.awt.image.BufferedImage;

/**
 * The renderer component allows for defining a texture for the node to be drawn to the screen. This is a component and has to be added to the node for it to be recognized. Only the last added renderer will be recognized on a node when rendering. <br><br>
 * The <strong><code>Renderer</code></strong> component allows for the <strong><code>getRender()</code></strong> function to be overriden and modified to create a custom render output. Here only a <strong><code>BufferedImage</code></strong> can be output using this function and in order to completely override the rendering of nodes, it can be done using the <strong><code>RenderLayer</code></strong> interface.
 */
public class Renderer extends Component {
    private Texture texture;
    private Material material;

    public final void setTexture(Texture image) {
        texture = image;
    }
    public final Texture getTexture() {
        return texture;
    }
    public final Material getMaterial() {
        return material;
    }
    public final void setMaterial(Material material) {
        this.material = material;
    }

    public enum Stage {
        NOTSTARTED,
        RENDERING,
        FINISHED,
        READYTOBIND,
        BOUND
    }
}