package KanapkaEngine.Editor;

import KanapkaEngine.Components.RenderLayer;
import KanapkaEngine.Components.RenderStage;

import java.awt.*;

public class EditorRenderLayer implements RenderLayer {
    @Override
    public void Render(Graphics2D main) {
        if (!Editor.isEditor()) return;
        main.setFont(main.getFont().deriveFont(30f));
        main.drawString("Editor", 300, 30);
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.FOREGROUND;
    }
}
