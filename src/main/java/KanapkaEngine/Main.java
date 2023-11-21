package KanapkaEngine;

import KanapkaEngine.Game.EngineConfiguration;
import KanapkaEngine.Game.GameLogic;
import KanapkaEngine.RenderLayers.Debug;

public class Main {
    public static void main(String[] args) {
        System.setProperty("env", "dev");

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

        engine.RegisterRenderLayer(new Debug());
    }
}