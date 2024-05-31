package KanapkaEngine.Components;

import java.awt.*;
import java.awt.geom.AffineTransform;

public interface Renderable {
    /**
     * Access point to the Render Thread where you are able to implement additional rendering using this interface. <br>
     * This function is called after drawing of the object. It can be used to create additional visuals for the object outside a renderer.
     * @param main The main drawing graphics of the renderer
     * @param transformation The transformation matrix of the node when drawing.
     */
    void onRender(Graphics2D main, AffineTransform transformation);
}
