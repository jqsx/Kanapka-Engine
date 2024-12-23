package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.ChunkLayer;
import KanapkaEngine.RenderLayers.NodeLayer;
import org.joml.Vector2d;

import java.awt.*;

public class Editor {
    private static boolean editor = false;

    public static void StartEditor() throws Exception {
        editor = true;

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
                RenderLayer.register(new ChunkLayer());

//                Texture texture = new Texture(ResourceLoader.loadResource("logo.png"));
//
//                material = new TextureMaterial();
//                material.MainTex = texture;
//
//                node = new Node();
//
//                Renderer renderer = new Renderer();
//                renderer.setMaterial(material);
//                renderer.setTexture(texture);
//
//                node.addComponent(renderer);
//
//                node.append();

                GenerateChunk(0, 0);
            }

            @Override
            public void Update() {
//                node.transform.setRotation((Math.cos((Time.time() % 1.0) * Math.PI) + 1.0) * Math.PI * 2.0);
            }

            @Override
            public void End() {

            }

            private void GenerateChunk(int x, int y) {
                Chunk chunk = Chunk.build(new Point(x, y), World.getCurrent());

                chunk.appendBlock(new Block(chunk, new Point(0, 0), 0));

                World.getCurrent().set(chunk);
            }
        }, engineConfiguration);
    }
}
