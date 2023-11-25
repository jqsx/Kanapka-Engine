package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.Camera;
import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class World implements RenderLayer {
    public static int Visible_Nodes = 0;
    public static boolean renderToGrid = false;
    @Override
    public void Render(Graphics2D main) {
        Visible_Nodes = 0;
        if (Camera.main == null) {
            return;
        }
        for (Node node : SceneManager.getSceneNodes()) {
            renderNode(main, node);
        }
    }

    private void renderNode(Graphics2D main, Node node) {
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Dimension window_bounds = Window.getWindowSize();
            if (!Engine.isMacOS()) window_bounds.setSize(window_bounds.width / 2.0, window_bounds.height / 2.0);
            Vector2D position = node.transform.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = node.transform.getSize();
            Rectangle boundingTextureBox = new Rectangle((int) position.getX() - (int) (render.getWidth() * size.getX()), (int) position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);
            Rectangle cameraView = new Rectangle((int) (camera_position.getX() - window_bounds.width / g_size), (int) (camera_position.getY() - window_bounds.height / g_size), (int) (window_bounds.width * 2 / g_size), (int) (window_bounds.height * 2 / g_size));

            if (cameraView.intersects(boundingTextureBox)) {
                AffineTransform at = new AffineTransform();
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate((camera_position.getX() - position.getX()) / size.getX(), (camera_position.getY() - position.getY()) / size.getY());
                main.drawImage(render, at, null);
                Visible_Nodes++;
            }
        }
        renderChildNodes(main, node);
    }

    private void renderChildNodes(Graphics2D main, Node node) {
        for (int i = 0; i < node.childCount(); i++) {
            renderNode(main, node.getChild(i));
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
