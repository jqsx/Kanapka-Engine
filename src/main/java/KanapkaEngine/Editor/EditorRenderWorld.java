package KanapkaEngine.Editor;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;

import java.awt.*;

public class EditorRenderWorld implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (!Editor.isEditor()) return;
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.WORLD;
    }
}
