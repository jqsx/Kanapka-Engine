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
        window_bounds = new Dimension(window_bounds.width * 2, window_bounds.height * 2);
        double g_size = SceneManager.getGlobalSize();
        cameraView.setBounds((int) (-window_bounds.width / 2.0), (int) (-window_bounds.height / 2.0), window_bounds.width, window_bounds.height);
        //cameraView.setBounds((int) (camera_position.getX() - window_bounds.width * 2 / g_size), (int) (camera_position.getY() - window_bounds.height * 2 / g_size), (int) (window_bounds.width * 4 / g_size), (int) (window_bounds.height * 4 / g_size));
    }

    private void n_renderNode(Graphics2D main, Node node) {
        if (node.getRenderer() == null) {
            renderChildNodes(main, node);
            return;
        }
        BufferedImage render = node.getRenderer().getRender();
        if (render != null) {
            AffineTransform at = getTransform(node);

            boundingTextureBox.setBounds((int) at.getTranslateX(), (int) at.getTranslateY(), (int) (at.getScaleX() * render.getWidth()), (int) (at.getScaleY() * render.getWidth()));

            if (cameraView.intersects(boundingTextureBox)) {
                main.drawImage(render, at, null);
                {
                    Vector2D screenPosition = new Vector2D(at.getTranslateX(), at.getTranslateY());
                    Vector2D size = new Vector2D(render.getWidth() * at.getScaleX(), render.getHeight() * at.getScaleY());
                    if (node instanceof Renderable renderable) {
                        renderable.onRender(main, screenPosition, size);
                    }
                    if (node.getRenderer() instanceof Renderable renderable) {
                        renderable.onRender(main, screenPosition, size);
                    }
                }
                //drawOutline(main, at, new Vector2D(render.getWidth(),render.getWidth()));
            }
        }
        renderChildNodes(main, node);
    }

    private void drawOutline(Graphics2D main, AffineTransform at, Vector2D scale) {
        double gSize = SceneManager.getGlobalSize();
        main.setStroke(new BasicStroke(5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        main.setColor(Color.black);
        main.drawRect((int) at.getTranslateX(), (int) at.getTranslateY(), (int) (at.getScaleX() * scale.getX()), (int) (at.getScaleY() * scale.getY()));
    }

    private AffineTransform getTransform(Node node) {
        AffineTransform at = new AffineTransform();

        assert node.getRenderer() != null;
        BufferedImage render = node.getRenderer().getRender();

        double gSize = SceneManager.getGlobalSize();

        Vector2D size = node.transform.getSize();
        size = new Vector2D(size.getX() / render.getWidth(), size.getY() / render.getHeight());
        Vector2D cameraPosition = Camera.main.getPosition();
        Vector2D position = cameraPosition.add(node.transform.getPosition());
        position = position.add(new Vector2D(-node.transform.getSize().getX() / 2.0, node.transform.getSize().getY() / 2.0));

        //position = new Vector2D(gSize / position.getX(), gSize / position.getY());

        at.scale(size.getX() * gSize, size.getY() * gSize);
        at.translate(position.getX() / size.getX(), -position.getY() / size.getY());
        at.rotate(node.transform.getRotation(), -size.getX() * gSize, -size.getY() * gSize);

        return at;
    }

    private void renderChildNodes(Graphics2D main, Node node) {
        for (int i = 0; i < node.childCount(); i++) {
            n_renderNode(main, node.getChild(i));
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
