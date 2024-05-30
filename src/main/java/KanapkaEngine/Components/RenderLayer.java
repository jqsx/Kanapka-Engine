package KanapkaEngine.Components;

import java.awt.*;

/**
 * A layer to the rendering stack to be rendered during the render process of the scene <br>
 * The entire has pre-made render layers that you are able to use instead of writting your own such as for rendering chunks and nodes.
 */
public interface RenderLayer {
    void Render(Graphics2D main);
    RenderStage getStage();
}
