package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.NodeLayer;
import KanapkaEngine.UI.Text;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Editor {
    private static boolean editor = false;

    public static void StartEditor() throws Exception {
        editor = true;
        EditorScene scene = new EditorScene();
        scene.load();

        SceneManager.loadScene(scene);

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = -1;
        engineConfiguration.width = 800;
        engineConfiguration.height = 600;
        engineConfiguration.window_title = "Editor";

        Engine engine = new Engine(new GameLogic() {
            private TextureMaterial material;

            private Node node;

            @Override
            public void Start() {
                RenderLayer.register(new NodeLayer());

                Texture texture = new Texture(ResourceLoader.loadResource("wooden.png"));

                material = new TextureMaterial();
                material.MainTex = texture;

                node = new Node();

                Renderer renderer = new Renderer();
                renderer.setMaterial(material);
                renderer.setTexture(texture);

                node.addComponent(renderer);

                node.append();
            }

            @Override
            public void Update() {
                node.transform.setPosition(new Vector2D(Math.cos(Time.time()), Math.sin(Time.time())));
            }

            @Override
            public void End() {

            }
        }, engineConfiguration);
    }

    public static boolean isEditor() {
        return editor;
    }
}
