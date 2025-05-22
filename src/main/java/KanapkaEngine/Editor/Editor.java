package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Component;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.ChunkLayer;
import KanapkaEngine.RenderLayers.NodeLayer;
import KanapkaEngine.RenderLayers.ParticleLayer;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.*;

public class Editor {
    private static boolean editor = false;

    private static Logger logger = new Logger("EDITOR");

    public static void BindEditor() {
        Engine engine = Engine.getMainInstance();

        if (engine == null)
            return;

        engine.load(new SimpleViewController());

        GuiRenderer guiRenderer = new GuiRenderer(new EditorDrawGUI());

        RenderLayer.register(guiRenderer);
        engine.load(guiRenderer);
    }

    public static void StartEditor() {
        editor = true;

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = -1;
        engineConfiguration.width = 800;
        engineConfiguration.height = 600;
        engineConfiguration.window_title = "Editor";

        Engine engine = new Engine(new GameLogic() {

            private double spawnDelay = 0;

            private double particleSpawnDelay = 0;

            private Texture texture;
            private Texture wooden;
            private ParticleSystem<Particle> particleSystem;

            @Override
            public void Start(Engine engine) {
                RenderLayer.register(new NodeLayer());
                RenderLayer.register(new ChunkLayer());
                RenderLayer.register(new ParticleLayer());

                SceneManager.getCurrentlyLoaded().isZSorted = false;

                BindEditor();

                texture = new Texture(ResourceLoader.loadImageResource("logo.png"));
                wooden = new Texture(ResourceLoader.loadImageResource("wooden.png"));

//                physicsTest();
//                particleSystemTest();

//                createSandwich(0, 0);
//                createSandwich(2, 0);
//                createSandwich(5, 0);
                GenerateChunk(0, 0);
//                GenerateChunk(1, 0);

                LookTexture("Basic Charakter Spritesheet.png", 4, 4);
                LookTexture("water from wateringcan frames.png", 8, 3).transform2D().setPosition(1, 0);
                LookTexture("chicken default.png", 8, 27).transform2D().setPosition(2, 0);
            }

            private Node LookTexture(String path, int width, int height) {
                Node node = new Node();

                Renderer renderer;
                node.addComponent(renderer = new Renderer());
                TextureMaterial material = new TextureMaterial();
                renderer.setMaterial(material);

                material.MainTex = new Texture(ResourceLoader.loadImageResource(path));
                material.spriteAtlasResolution.set(width, height);
                node.addComponent(new EditorComponent());

                node.append();
                return node;
            }

            private void particleSystemTest() {
                particleSystem = new ParticleSystem<>();

                TextureMaterial material = new TextureMaterial();

                material.MainTex = wooden;

                particleSystem.setMaterial(material);

                Node sys = new Node();

                sys.addComponent(particleSystem);

                sys.append();

                sys.addChild(new Node());
            }

            private void createSandwich(double x, double y) {
                Node node = new Node();

                node.transform2D().setPosition(x, y);

                TextureMaterial material = new TextureMaterial();
                material.MainTex = texture;

                Renderer renderer = new Renderer();
                renderer.setMaterial(material);

                node.addComponent(renderer);

                node.append();
            }

            private void physicsTest() {

                TextureMaterial material = new TextureMaterial();

                material.MainTex = wooden;

                {
                    Node floor = new Node();
                    floor.addComponent(new RectangleCollider());

                    Renderer renderer = new Renderer();

                    renderer.setMaterial(material);

                    floor.addComponent(renderer);

                    floor.transform2D().setSize(3, 1);

                    floor.append();
                }

                {
                    Node box = new Node();
                    box.addComponent(new RectangleCollider());
//                    box.addComponent(new Rigidbody());

                    Renderer renderer = new Renderer();

                    renderer.setMaterial(material);

                    box.addComponent(renderer);

                    box.transform2D().setPosition(0, 3);

                    box.append();
                }

            }

            @Override
            public void Update() {

                if (spawnDelay < Time.time() && Input.isKeyDown(GLFW_KEY_SPACE)) {
                    spawnDelay = Time.time() + 0.1;

                    Vector2d wL = Input.getWorldMousePosition();
                    createSandwich(wL.x, wL.y);
                }

                if (particleSystem != null)
                    if (particleSpawnDelay < Time.time()) {
                        particleSpawnDelay = Time.time() + 0.05;

                        Particle particle = particleSystem.emit(new Vector2d(0, 0));

                        if (particle != null)
                            particle.addVelocity(new Vector2d(10, 0));
                    }
            }

            @Override
            public void End() {

            }

            private void GenerateChunk(int x, int y) {
                Chunk chunk = Chunk.build(new Point(x, y), World.getCurrent());

                for (int _x = 0; _x < SceneManager.getCurrentlyLoaded().getChunkSize(); _x++) {
                    for (int _y = 0; _y < SceneManager.getCurrentlyLoaded().getChunkSize(); _y++) {
                        if ((_x + _y) % 2 == 0)
                            chunk.createBlock(0, new Point(_x, _y));
                    }
                }

                chunk.ready();
                chunk.activate();

                World.getCurrent().set(chunk);
            }
        }, engineConfiguration);
    }
}
