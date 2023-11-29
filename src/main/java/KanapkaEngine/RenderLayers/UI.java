package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;

import java.awt.*;

public class UI implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (KanapkaEngine.UI.UI.currentlyDisplayed != null)
            KanapkaEngine.UI.UI.currentlyDisplayed.Render(main);
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }
}
