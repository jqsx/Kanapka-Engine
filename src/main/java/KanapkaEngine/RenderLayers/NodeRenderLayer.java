package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class NodeRenderLayer implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        SceneManager.getSceneNodes().foreach(node -> RenderNode(main, node));
    }

    private void RenderNode(Graphics2D main, Node node) {
        Renderer renderer = node.getRenderer();

        if (renderer != null) {
            BufferedImage image = renderer.getRender();

            main.drawImage(image, getTransformationNode(node, image), null);
        }
    }

    private AffineTransform getTransformationNode(Node node, BufferedImage image) {
        AffineTransform at = new AffineTransform();

        Vector2D position = node.transform.getPosition();

        at.translate(position.getX(), position.getY());

        return at;
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
