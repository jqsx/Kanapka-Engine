package KanapkaEngine.Editor;

import KanapkaEngine.Components.Camera;
import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.SimpleViewController;
import KanapkaEngine.Components.SpriteRenderer;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;

import java.awt.*;

public class Editor {
    private static boolean editor = false;
    public static void StartEditor() {
        editor = true;
        EditorScene scene = new EditorScene("editor");
        scene.load();

        SceneManager.loadScene(scene);

        Camera camera = new Camera();

        Node test = Node.build();
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setTexture("objects.png");
        test.addComponent(spriteRenderer);

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = 120;
        engineConfiguration.width = 800;
        engineConfiguration.height = 600;
        Engine engine = new Engine(new GameLogic() {
            @Override
            public void Start() {

            }

            @Override
            public void Update() {

            }

            @Override
            public void End() {

            }
        }, engineConfiguration);

        engine.getWindow().setWorldBackdrop(new Color(99, 153, 107));

        SimpleViewController controller = new SimpleViewController();
        EditorActions editorActions = new EditorActions();

        engine.load(controller);
        engine.load(editorActions);

        engine.InitializeLayers();
        engine.RegisterRenderLayer(new EditorRenderLayer());
        engine.RegisterRenderLayer(new EditorRenderWorld());
    }

    public static boolean isEditor() {
        return editor;
    }
}
