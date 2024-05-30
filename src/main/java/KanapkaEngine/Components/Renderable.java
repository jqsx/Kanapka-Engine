package KanapkaEngine.Components;

import java.awt.geom.AffineTransform;

public interface Renderable {
    /**
     * Access point to the Render Thread where you are able to implement additional rendering using this interface.
     * @param transformation
     */
    void onRender(AffineTransform transformation);
}
