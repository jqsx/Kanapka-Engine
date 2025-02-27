package KanapkaEngine.RenderLayers;

import KanapkaEngine.Game.*;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.Renderer;

import java.util.ArrayList;
import java.util.List;

public class NodeLayer implements RenderLayer {

    private static final List<Renderer> renderers = new ArrayList<>();

    private static final Logger logger = new Logger("NodeLayer");

    public static boolean DRAW_WIREFRAME = false;

    @Override
    public void Render() {
        if (SceneManager.hasScene() && Camera.main != null) {
            SceneManager.getSceneNodes().forEach(this::RenderNode);
        }
    }

    private void RenderNode(Node node) {
        node.getComponents(renderers, Renderer.class);
        for (Renderer renderer : renderers) {
            if (Camera.main.isWithin(renderer)) {
                if (DRAW_WIREFRAME) {
                    Graphics.DrawWireframe(node.transform.getTransformation());
                }
                else Graphics.DrawSprite(renderer.getTexture(), node.transform.getTransformation(), renderer.getMaterial());
            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
