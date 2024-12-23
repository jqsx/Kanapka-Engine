package KanapkaEngine.RenderLayers;

import KanapkaEngine.Game.Node;
import KanapkaEngine.Game.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Camera;
import KanapkaEngine.Game.Graphics;
import KanapkaEngine.Game.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class NodeLayer implements RenderLayer {

    private static final List<Renderer> renderers = new ArrayList<>();

    @Override
    public void Render() {
        if (SceneManager.hasScene() && Camera.main != null) {
            SceneManager.getSceneNodes().forEach(this::RenderNode);
        }
    }

    private void RenderNode(Node node) {
        node.getComponents(renderers, Renderer.class);
        for (Renderer renderer : renderers) {
            Graphics.DrawSprite(renderer.getTexture(), node.transform.getTransformation(), renderer.getMaterial());
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
