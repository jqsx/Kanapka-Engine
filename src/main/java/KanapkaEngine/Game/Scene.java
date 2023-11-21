package KanapkaEngine.Game;

import KanapkaEngine.Components.Node;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String loadPath;
    private boolean isLoaded = false;
    public final List<Node> nodes = new ArrayList<>();

    public Scene(String sceneFilePath) {
        this.loadPath = sceneFilePath;
    }

    public final void unload() {
        nodes.clear();
        isLoaded = false;
    }

    public final void load() {
        isLoaded = true;
    }

    public final boolean loaded() {
        return isLoaded;
    }
}
