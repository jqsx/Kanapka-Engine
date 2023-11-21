package KanapkaEngine.RenderLayers;

import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;
import KanapkaEngine.Game.SceneManager;

import java.awt.*;

public class World implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        for (Node node : SceneManager.getSceneNodes()) {

        }
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
