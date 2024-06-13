package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;

public interface Renderable {
    /**
     * Access point to the Render Thread where you are able to implement additional rendering using this interface. <br>
     * This function is called after drawing of the object. It can be used to create additional visuals for the object outside a renderer.
     * @param main The main drawing graphics of the renderer
     * @param screenPosition The transformation matrix of the node when drawing.
     */
    void onRender(Graphics2D main, Vector2D screenPosition, Vector2D screenSize);
}
