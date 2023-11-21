package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;

import java.awt.*;

public class Particles implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {

    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
