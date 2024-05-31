package KanapkaEngine.Components;

import java.awt.image.BufferedImage;

/**
 * The renderer component allows for defining a texture for the node to be drawn to the screen. This is a component and has to be added to the node for it to be recognized. Only the last added renderer will be recognized on a node when rendering. <br><br>
 * The <strong><code>Renderer</code></strong> component allows for the <strong><code>getRender()</code></strong> function to be overriden and modified to create a custom render output. Here only a <strong><code>BufferedImage</code></strong> can be output using this function and in order to completely override the rendering of nodes, it can be done using the <strong><code>RenderLayer</code></strong> interface.
 */
public class Renderer extends Component {
    BufferedImage rendered_visual;
    int loading_state = NOT_STARTED;

    public BufferedImage getRender() {
        return rendered_visual;
    }

    public final void setTexture(BufferedImage image) {
        rendered_visual = image;
    }
    public final BufferedImage getTexture() {
        return rendered_visual;
    }
    public final int getRenderState() {
        return loading_state;
    }
    public final static int NOT_STARTED = 0;
    public final static int STARTED = 1;
    public final static int FINISHED = 2;
}