package KanapkaEngine;

import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.RenderLayers.Debug;

public class Main {
    public static void main(String[] args) {
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
        });

        engine.RegisterRenderLayer(new Debug());
    }
}