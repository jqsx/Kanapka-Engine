package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Game.Renderer;
import KanapkaEngine.Time;

import java.awt.*;

public class Debug implements RenderLayer {
    public Debug() {

    }
    @Override
    public void Render(Graphics2D main) {
        drawText(main, "Game MS " + (Time.deltaTime() * 1000.0), new Point(0, 20));
        drawText(main, "FPS " + Renderer.getFPS(), new Point(0, 40));
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
