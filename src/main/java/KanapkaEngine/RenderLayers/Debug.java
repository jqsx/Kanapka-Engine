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
        drawText(main, "Game MS " + Math.round(Time.deltaTime() * 1000.0), new Point(0, 30));
        drawText(main, "Renderer MS " + Math.round(Renderer.getDelta() * 1000.0), new Point(0, 60));
        drawText(main, "Total MS " + Math.round((Renderer.getDelta() + Time.deltaTime()) * 1000.0), new Point(0, 90));
        drawText(main, "FPS " + Renderer.getFPS(), new Point(0, 120));

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
        main.setFont(main.getFont().deriveFont(30f));

        main.drawString(text, point.x, point.y);
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }
}
