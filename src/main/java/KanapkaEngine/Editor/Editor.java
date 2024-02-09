package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

public class Editor {
    private static boolean editor = false;
    public static void StartEditor() throws Exception {
        editor = true;
        EditorScene scene = new EditorScene();
        scene.load();

        SceneManager.loadScene(scene);

        TextureAtlas atlas = new TextureAtlas(ResourceLoader.loadResource("objects.png"));
        atlas.createSubTexture("all", new Rectangle(16, 16));

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = 120;
        engineConfiguration.width = 800;
        engineConfiguration.height = 600;
        engineConfiguration.window_title = "Editor";
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

        {
            Node node = Node.build();
            SpriteRenderer renderer = new SpriteRenderer();
            renderer.setTexture("wooden.png");
            node.addComponent(renderer);
            node.addComponent(new Rigidbody());
            node.addComponent(new Collider());
            node.transform.setSize(new Vector2D(16, 16));
            node.transform.setPosition(new Vector2D(0, 50));
        }

        {
            Node node = Node.build();
            SpriteRenderer renderer = new SpriteRenderer();
            renderer.setTexture("wooden.png");
            node.addComponent(renderer);
            node.addComponent(new Collider());
            node.transform.setPosition(new Vector2D(23.0, 20));
            node.transform.setSize(new Vector2D(32, 16));
        }

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
