package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Engine;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.UI.Text;
import KanapkaEngine.UI.UI;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;

import java.awt.*;

public class Editor {
    private static boolean editor = false;
    public static void StartEditor() {
        editor = true;
        EditorScene scene = new EditorScene();
        scene.load();

        SceneManager.loadScene(scene);

        TextureAtlas atlas = new TextureAtlas(TextureLoader.loadResource("objects.png"));
        atlas.createSubTexture("all", new Rectangle(16, 16));

        {
            Node test = Node.build();
            SpriteRenderer spriteRenderer = new SpriteRenderer();
            spriteRenderer.setTexture(atlas.getSubTexture("all"));
            RectCollider collider = new RectCollider();
            collider.setSize(new Vector2D(16, 16));
            test.addComponent(spriteRenderer);
            test.addComponent(collider);
            Rigidbody rigidbody = new Rigidbody();
            test.addComponent(rigidbody);
        }

        {
            Node test = Node.build();
            SpriteRenderer spriteRenderer = new SpriteRenderer();
            spriteRenderer.setTexture(atlas.getSubTexture("all"));
            RectCollider collider = new RectCollider();
            collider.setSize(new Vector2D(16, 16));
            test.addComponent(spriteRenderer);
            test.addComponent(collider);
            Rigidbody rigidbody = new Rigidbody();
            test.addComponent(rigidbody);
//            rigidbody.getBody().setLinearVelocity((0.5 - Math.random()) * 10.0, (0.5 - Math.random()) * 10.0);
            rigidbody.getBody().translate(0, 30);
        }

        {
            Node floor = Node.build();
            floor.name = "floor";
            SpriteRenderer spriteRenderer = new SpriteRenderer();
            spriteRenderer.setTexture(atlas.getSubTexture("all"));
            RectCollider collider = new RectCollider();
            collider.setSize(new Vector2D(100, 16));
            floor.transform.setSize(new Vector2D(100.0 / 16, 1));
            floor.addComponent(spriteRenderer);
            floor.addComponent(collider);
            Rigidbody rigidbody = new Rigidbody();
            floor.addComponent(rigidbody);
            rigidbody.getBody().setMass(MassType.INFINITE);
            rigidbody.getBody().translate(-50, -16);
        }

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

        UI.currentlyDisplayed = new UI();
        {
            Text text = new Text();
            text.setFont(engine.getWindow().getFont());
            text.setText("bobux");
            text.setColor(Color.red);
            text.setParent(UI.currentlyDisplayed);
            text.pivot = new Point(-1, -1);
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
