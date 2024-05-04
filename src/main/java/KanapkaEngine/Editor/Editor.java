package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Component;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.Game.Window;
import KanapkaEngine.RenderLayers.TestWorldDraw;
import KanapkaEngine.UI.Image;
import KanapkaEngine.UI.Text;
import KanapkaEngine.UI.UI;
import KanapkaEngine.UI.UIComponent;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javax.tools.Tool;
import java.awt.*;
import java.util.Arrays;

public class Editor {
    private static boolean editor = false;
    public static void StartEditor() throws Exception {
        editor = true;
        EditorScene scene = new EditorScene();
        scene.load();

        Physics.gravity = Physics.gravity.scalarMultiply(3);

        SceneManager.loadScene(scene);

        TextureAtlas atlas = new TextureAtlas(ResourceLoader.loadResource("objects.png"));
        atlas.createSubTexture("all", new Rectangle(16, 16));

        BlockManager.createBlock(new BlockData("grass.png"));

        BlockManager.getBlockData(1).hasCollision = false;

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = 9999;
        int screenHeight = (int)Math.round(Toolkit.getDefaultToolkit().getScreenSize().height / 1.5);
        engineConfiguration.width = screenHeight;
        engineConfiguration.height = screenHeight;
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
            Node node = new Node();

            node.addComponent(new Collider());
            node.addComponent(new Rigidbody());
            node.addComponent(new Renderer());

            node.getRenderer().setTexture(ResourceLoader.loadResource("wooden.png"));

            node.transform.setSize(new Vector2D(16, 16));
            node.transform.setPosition(new Vector2D(20, 200));

            node.addComponent(new Component() {
                @Override
                public void Update() {
                    if (node.isAlive()) {
                        if (node.transform.getPosition().getY() < -500)
                            node.Destroy();
                    }
                }
            });

            node.getRigidbody().setBounce(0);

            node.append();
        }

        {
            World world = SceneManager.getCurrentlyLoaded().scene_world;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    Chunk chunk = Chunk.build(new Point(i, j), world);

                    AddBlocks(chunk);
                }
            }
        }

//        engine.getWindow().setWorldBackdrop(new Color(99, 153, 107));
        engine.getWindow().setWorldBackdrop(new Color(0, 0, 107));

        SimpleViewController controller = new SimpleViewController();
        EditorActions editorActions = new EditorActions();

        engine.load(controller);
        engine.load(editorActions);

//        engine.InitializeLayers();
//        engine.RegisterRenderLayer(new EditorRenderLayer());
//        engine.RegisterRenderLayer(new EditorRenderWorld());
        engine.RegisterRenderLayer(new TestWorldDraw());
    }

    private static void AddBlocks(Chunk chunk) {
        int m = SceneManager.getCurrentlyLoaded().getChunkSize();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (Mathf.aDistance(new Vector2D(30, 60), chunk.getPosition().add(new Vector2D(i, -j).scalarMultiply(Chunk.BLOCK_SCALE))) > 160) {
                    new Block(chunk, new Point(i, j));
                }
            }
        }
        chunk.ready();
    }

    public static boolean isEditor() {
        return editor;
    }
}
