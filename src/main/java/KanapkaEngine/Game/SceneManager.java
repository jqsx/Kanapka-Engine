package KanapkaEngine.Game;

import KanapkaEngine.Components.Node;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static Scene currentlyLoaded;

    private SceneManager() {

    }

    public static Scene getCurrentlyLoaded() {
        return currentlyLoaded;
    }

    public static List<Node> getSceneNodes() {
        if (currentlyLoaded != null)
            return currentlyLoaded.nodes;
        return new ArrayList<>();
    }

    public static Scene loadScene(Scene scene) {
        if (scene.loaded())
            return currentlyLoaded = scene;
        System.out.println("Scene isn't loaded");
        return null;
    }
}
