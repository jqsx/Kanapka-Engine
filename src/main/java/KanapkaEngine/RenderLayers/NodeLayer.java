package KanapkaEngine.RenderLayers;

import KanapkaEngine.Game.Node;
import KanapkaEngine.Game.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Camera;
import KanapkaEngine.Game.Graphics;
import KanapkaEngine.Game.SceneManager;

public class NodeLayer implements RenderLayer {
    @Override
    public void Render() {
        if (SceneManager.hasScene() && Camera.main != null) {
            SceneManager.getSceneNodes().foreach(this::RenderNode);
        }
    }

    private void RenderNode(Node node) {
        Renderer renderer = node.getRenderer();
        if (renderer != null) {
            Graphics.DrawSprite(renderer.getTexture(), node.transform.getTransformation(), renderer.getMaterial());
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
