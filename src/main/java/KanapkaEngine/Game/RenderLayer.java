package KanapkaEngine.Game;

import KanapkaEngine.Components.RenderStage;

/**
 * A layer to the rendering stack to be rendered during the render process of the scene <br>
 * The entire has pre-made render layers that you are able to use instead of writting your own such as for rendering chunks and nodes.
 */
public interface RenderLayer {
    /**
     * Main graphics rendering interface where the render layer is able to access the Graphics2D component of the renderer.
     */
    void Render();

    /**
     *
     * @return The internal stage telling the renderer in what order stack should this layer be rendered.
     */
    RenderStage getStage();

    /**
     * Register new render layer to the currently active engine
     * @param layer
     */
    static void register(RenderLayer layer) {
        Engine.registerLayer(layer);
    }
}
