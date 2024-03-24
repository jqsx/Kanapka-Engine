package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Game.Renderer;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import KanapkaEngine.Time;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static KanapkaEngine.Game.Window.getWindowSize;

public class Debug implements RenderLayer {
    public Debug() {

    }
    @Override
    public void Render(Graphics2D main) {
        drawText(main, "Game MS " + (Time.deltaTime() * 1000.0), new Point(0, 20));
        drawText(main, "FPS " + Renderer.getFPS(), new Point(0, 40));

        drawText(main, "Visible Nodes " + World.Visible_Nodes + " / " + Node.getNodeCount(), new Point(100, 40));
        drawText(main, "Visible Chunks " + Chunks.VisibleChunks, new Point(400, 40));

        {
            Dimension screen = Window.getWindowSize();

            main.setColor(Color.red);
            main.fillArc((int) (screen.width - 10f), (int) (screen.height - 10f), (int) 20f, (int) 20f, 0, 360);
        }

        if (Camera.main == null) {
            main.setColor(Color.red);
            main.setFont(main.getFont().deriveFont(20f));
            Rectangle2D rect = main.getFont().getStringBounds("No cameras drawing.", main.getFontRenderContext());
            main.drawString("No cameras drawing.", (int) (getWindowSize().width / 2 - rect.getWidth() / 2), (int) (getWindowSize().height / 2 - rect.getHeight() / 2));
            main.drawArc(0, 0, getWindowSize().width, getWindowSize().height, 0, 360);
        }
    }

    private void drawText(Graphics2D main, String text, Point point) {
        Color color = new Color((int) (Math.abs(Math.cos(point.x + point.y)) * 255), (int) (Math.abs(Math.sin(point.x + point.y)) * 255), (int) (Math.abs(Math.sin(point.x - point.y)) * 255));

        main.setColor(color);
        main.setFont(main.getFont().deriveFont(20f));

        main.drawString(text, point.x, point.y);
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }
}
