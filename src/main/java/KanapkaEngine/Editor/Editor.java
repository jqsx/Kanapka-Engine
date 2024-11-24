package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.UI.Text;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

public class Editor {
    private static boolean editor = false;
    static Text text;
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
                {
                    text = new Text();
                    text.setText("Big boy");

                    text.setSize(30);
                }
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

            node.addComponent(new Renderer());

            node.getRenderer().setTexture(ResourceLoader.loadResource("wooden.png"));

            node.transform.setSize(new Vector2D(16, 16));

            node.addComponent(new Collider());

            node.append();
        }

        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                createSampleChunk(new Point(i, j));
            }
        }

        SimpleViewController controller = new SimpleViewController();
        EditorActions editorActions = new EditorActions();

        engine.load(controller);
        engine.load(editorActions);
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

    private static void createSampleChunk(Point at) {
        Chunk chunk = Chunk.build(at, World.getCurrent());

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (Mathf.Noise.noise(i + at.x * 10, j + at.y * 10) > 0.2)
                    chunk.createBlock(0, new Point(i, j));
            }
        }
    }

    public static boolean isEditor() {
        return editor;
    }
}
