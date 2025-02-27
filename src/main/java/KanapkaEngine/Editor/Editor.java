package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Component;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Editor.Attributes.*;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.Game.Window;
import KanapkaEngine.RenderLayers.ChunkLayer;
import KanapkaEngine.RenderLayers.NodeLayer;
import KanapkaEngine.RenderLayers.ParticleLayer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.*;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Editor {
    private static boolean editor = false;

    private static Logger logger = new Logger("EDITOR");

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

                engine.load(new SimpleViewController());

                GuiRenderer guiRenderer = new GuiRenderer(new EditorDrawGUI());

                RenderLayer.register(guiRenderer);
                engine.load(guiRenderer);

                texture = new Texture(ResourceLoader.loadImageResource("logo.png"));
                wooden = new Texture(ResourceLoader.loadImageResource("wooden.png"));

//                physicsTest();
                particleSystemTest();

//                GenerateChunk(0, 0);
//                GenerateChunk(1, 0);
            }

            private void particleSystemTest() {
                particleSystem = new ParticleSystem<>();

                particleSystem.setTexture(texture);

                particleSystem.setInstanced();

                Node sys = new Node();

                sys.addComponent(particleSystem);

                sys.append();

                sys.addChild(new Node());
            }

            private void createSandwich(double x, double y) {
                Node node = new Node();

                node.transform.setPosition(x, y);

                TextureMaterial material = new TextureMaterial();
                material.MainTex = texture;

                Renderer renderer = new Renderer();
                renderer.setMaterial(material);
                renderer.setTexture(texture);

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
                    renderer.setTexture(wooden);

                    floor.addComponent(renderer);

                    floor.transform.setSize(3, 1);

                    floor.append();
                }

                {
                    Node box = new Node();
                    box.addComponent(new RectangleCollider());
                    box.addComponent(new Rigidbody());

                    Renderer renderer = new Renderer();

                    renderer.setMaterial(material);
                    renderer.setTexture(wooden);

                    box.addComponent(renderer);

                    box.transform.setPosition(0, 3);

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

                        particleSystem.emit(new Vector2d(0, 0)).addVelocity(new Vector2d(10, 0));

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
