package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.*;
import KanapkaEngine.Game.SceneManager;

import java.awt.*;
import java.awt.image.RescaleOp;

public class Particles implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        for (Node node : SceneManager.getSceneNodes()) {
            Renderer renderer = node.getRenderer();
            if (renderer instanceof ParticleSystem particleSystem) {

            }
        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.PARTICLES;
    }
}
