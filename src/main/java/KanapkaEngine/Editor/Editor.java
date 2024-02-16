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
        engineConfiguration.FPSLIMIT = 9999;
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
            World world = SceneManager.getCurrentlyLoaded().scene_world;
            for (int i = 0; i < 9; i++) {
                int x = i % 2;
                int y = (int)Math.floor(i / 2.0);
                Chunk chunk = Chunk.build(new Point(x - 1, y - 1), world);
                {
                    Block block = new Block(chunk, new Point(0, 0));
                    block.setImage(ResourceLoader.loadResource("wooden.png"));
                    block.append();
                }
                {
                    Block block = new Block(chunk, new Point(9, 9));
                    block.setImage(ResourceLoader.loadResource("wooden.png"));
                    block.append();
                }
                {
                    Block block = new Block(chunk, new Point(9, 0));
                    block.setImage(ResourceLoader.loadResource("wooden.png"));
                    block.append();
                }
                {
                    Block block = new Block(chunk, new Point(0, 9));
                    block.setImage(ResourceLoader.loadResource("wooden.png"));
                    block.append();
                }

                chunk.ready();
            }
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
