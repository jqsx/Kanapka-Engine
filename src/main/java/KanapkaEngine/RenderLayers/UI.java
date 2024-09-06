package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;

import java.awt.*;
import java.util.ConcurrentModificationException;

public class UI implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        try {
            if (KanapkaEngine.UI.UI.currentlyDisplayed != null)
                KanapkaEngine.UI.UI.currentlyDisplayed.Render(main);
        } catch (ConcurrentModificationException e) {

        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.UI;
    }
}
