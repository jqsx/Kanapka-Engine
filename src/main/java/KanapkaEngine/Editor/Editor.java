package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.Game.Window;
import KanapkaEngine.UI.Image;
import KanapkaEngine.UI.Text;
import KanapkaEngine.UI.UI;
import KanapkaEngine.UI.UIComponent;
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

        SceneManager.loadScene(scene);

        TextureAtlas atlas = new TextureAtlas(ResourceLoader.loadResource("objects.png"));
        atlas.createSubTexture("all", new Rectangle(16, 16));

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

//        {
//            Text text = new Text();
//            text.setText("Text");
//            text.setColor(Color.red);
//            text.setSize(50f);
//            text.pivot = UIComponent.Pivot.Left;
//        }
//
//        {
//            Node logo = Node.build();
//            SpriteRenderer renderer = new SpriteRenderer();
//            renderer.setTexture("logo.png");
//            logo.addComponent(renderer);
//            logo.transform.setSize(new Vector2D(100.0, 100.0));
//        }
//
//        {
//            Image image = new Image();
//            image.setImage("test.jpg");
//        }

        {
            Node node = Node.build();
            SpriteRenderer renderer = new SpriteRenderer();
            renderer.setTexture("wooden.png");
            node.addComponent(renderer);
            Rigidbody rigidbody = new Rigidbody();
            Collider collider = new Collider();
            node.addComponent(rigidbody);
            node.addComponent(collider);

            node.transform.setSize(new Vector2D(16, 16));

            node.transform.setPosition(new Vector2D(0, 50));
        }

        {
            Node node = Node.build();
            SpriteRenderer renderer = new SpriteRenderer();
            renderer.setTexture("wooden.png");
            node.addComponent(renderer);
            Collider collider = new Collider();
            node.addComponent(collider);

            node.transform.setSize(new Vector2D(16, 16));

            node.transform.setPosition(new Vector2D(0, -20));
        }

//        engine.getWindow().setResizable(false);

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
