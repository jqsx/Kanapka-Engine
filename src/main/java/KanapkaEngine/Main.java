package KanapkaEngine;

import KanapkaEngine.Components.*;
import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import KanapkaEngine.RenderLayers.Debug;
import KanapkaEngine.RenderLayers.World;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.setProperty("env", "dev");

        Scene scene = new Scene("test");
        scene.load();

        SceneManager.loadScene(scene);

        Camera camera = new Camera();

        TextureAtlas atlas = new TextureAtlas(TextureLoader.loadResource("objects.png"));
        atlas.createSubTexture("test", new Rectangle(0, 80, 48, 64));
        BufferedImage grass_texture = TextureLoader.loadResource("grass.png");

        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                Node grass = Node.build();
                SpriteRenderer grassspriteRenderer = new SpriteRenderer();
                grass.transform.setSize(new Vector2D(48 / 16.0, 64 / 16.0));
                grass.addComponent(grassspriteRenderer);
                grassspriteRenderer.setTexture(grass_texture);
                grass.transform.setPosition(new Vector2D(i * 48, j * 64));
                if (new Random().nextDouble() > 0.5) {
                    Node node = Node.build(grass);
                    SpriteRenderer spriteRenderer = new SpriteRenderer();
                    node.addComponent(spriteRenderer);
                    spriteRenderer.setTexture(atlas.getSubTexture("test"));

                    node.transform.setPosition(new Vector2D(i * 48, j * 64));
                }
            }
        }

        EngineConfiguration engineConfiguration = new EngineConfiguration();
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

        SimpleViewController controller = new SimpleViewController();

        engine.load(controller);

        engine.RegisterRenderLayer(new Debug());
        engine.RegisterRenderLayer(new World());
    }
}