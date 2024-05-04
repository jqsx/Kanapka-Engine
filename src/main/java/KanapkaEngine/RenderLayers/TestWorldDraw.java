package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Game.SceneManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TestWorldDraw implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (Camera.main == null) {
            return;
        }

        SceneManager.getSceneNodes().foreach(node -> {
            renderNode(node, main);
        });

    }

    private void renderNode(Node node, Graphics2D main) {
        if (node.getRenderer() == null)
            return;

        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            main.drawImage(render, node.transform.worldToScreenTransform(), null);
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
