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

        Scene scene = new Scene("test");
        scene.load();

        SceneManager.loadScene(scene);

        Camera camera = new Camera();

        BufferedImage test = TextureLoader.loadResource("objects.png");
        System.out.println(test);
        TextureAtlas atlas = new TextureAtlas(test);
        atlas.createSubTexture("test", new Rectangle(0, 80, 48, 64));
        atlas.createSubTexture("bush", new Rectangle(96, 112, 32, 32));
        BufferedImage grass_texture = TextureLoader.loadResource("grass.png");

        int _size = 10;

        for (int i = -_size; i <= _size; i++) {
            for (int j = -_size; j <= _size; j++) {
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
                    spriteRenderer.setTexture(atlas.getSubTexture(new Random().nextDouble() > 0.5 ? "test" : "bush"));
                }
            }
        }

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = 120;
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

        engine.InitializeLayers();
    }
}