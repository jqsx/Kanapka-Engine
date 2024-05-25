package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class World implements RenderLayer {
    public static int Visible_Nodes = 0;
    public static boolean renderToGrid = false;

    private final Rectangle cameraView = new Rectangle();
    private final Rectangle boundingTextureBox = new Rectangle();
    @Override
    public void Render(Graphics2D main) {
        Visible_Nodes = 0;
        recalculateCameraView();
        if (Camera.main == null) {
            return;
        }

        try {
            SceneManager.getSceneNodes().foreach(node -> n_renderNode(main, node));
        } catch (ConcurrentModificationException | NullPointerException | ArrayIndexOutOfBoundsException ignore) {

        }
    }

    private void recalculateCameraView() {
        if (Camera.main == null) return;
        Vector2D camera_position = Camera.main.getPosition();
        Dimension window_bounds = KanapkaEngine.Game.Window.getWindowSize();
        if (!Engine.isMacOS()) window_bounds.setSize(window_bounds.width / 2.0, window_bounds.height / 2.0);
        double g_size = SceneManager.getGlobalSize();
        cameraView.setBounds((int) (camera_position.getX() - window_bounds.width / g_size), (int) (camera_position.getY() - window_bounds.height / g_size), (int) (window_bounds.width * 2 / g_size), (int) (window_bounds.height * 2 / g_size));
    }

    private void renderNode(Graphics2D main, Node node) {
        if (node.getRenderer() == null) {
            renderChildNodes(main, node);
            return;
        }
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            Vector2D camera_position = Camera.main.getPosition();
            Vector2D position = node.transform.getPosition().subtract(new Vector2D(render.getWidth() / 2.0, render.getHeight() / 2.0));
            double g_size = SceneManager.getGlobalSize();
            Vector2D size = node.transform.getSize();
            size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
            boundingTextureBox.setBounds((int) -position.getX() - (int) (render.getWidth() * size.getX()), (int) -position.getY() - (int) (render.getHeight() * size.getY()), (int) (render.getWidth() * size.getX()) * 2, (int) (render.getHeight() * size.getY()) * 2);
            if (cameraView.intersects(boundingTextureBox)) {
                Vector2D pos = new Vector2D((camera_position.getX() + position.getX()) / size.getX(), -(camera_position.getY() + position.getY() + node.transform.getSize().getY() / 2.0) / size.getY());
                AffineTransform at = new AffineTransform();
                double rad = Math.toRadians(node.transform.getRotation());
                at.scale(size.getX() * g_size, size.getY() * g_size);
                at.translate(pos.getX(), pos.getY());
                at.rotate(rad, render.getWidth() / 2.0, render.getHeight() / 2.0);
                main.drawImage(render, at, null);
                Visible_Nodes++;
            }
        }
        renderChildNodes(main, node);
    }

    private void n_renderNode(Graphics2D main, Node node) {
        if (node.getRenderer() == null) {
            renderChildNodes(main, node);
            return;
        }
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            AffineTransform at = getTransform(node);
            main.drawImage(render, at, null);
            drawOutline(main, at, new Vector2D(render.getWidth() - 1, render.getHeight() - 1));
        }
    }

    private void drawOutline(Graphics2D main, AffineTransform at, Vector2D scale) {
        double gSize = SceneManager.getGlobalSize();
        main.setStroke(new BasicStroke(5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        main.setColor(Color.black);
        main.drawRect((int) at.getTranslateX(), (int) at.getTranslateY(), (int) (at.getScaleX() + scale.getX() * gSize), (int) (at.getScaleY() + scale.getY() * gSize));
    }

    private AffineTransform getTransform(Node node) {
        AffineTransform at = new AffineTransform();

        assert node.getRenderer() != null;
        BufferedImage render = node.getRenderer().getRender();

        double gSize = SceneManager.getGlobalSize();

        Vector2D size = node.transform.getSize();
        size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
        Vector2D cameraPosition = Camera.main.getPosition();
        Vector2D position = new Vector2D(Math.round(cameraPosition.getX() * gSize) / gSize, Math.round(cameraPosition.getY() * gSize) / gSize).add(node.transform.getPosition());
        position = position.add(new Vector2D(0, node.transform.getSize().getY() / 2.0));

        //position = new Vector2D(gSize / position.getX(), gSize / position.getY());

        at.scale(size.getX() * gSize, size.getY() * gSize);
        at.translate(position.getX() / size.getX(), -position.getY() / size.getY());

        return at;
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
