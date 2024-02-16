package KanapkaEngine.Game;

import KanapkaEngine.Components.Node;
import KanapkaEngine.Components.World;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String loadPath;
    private boolean isLoaded = false;
    public final List<Node> nodes = new ArrayList<>();
    public final World scene_world;

    private int CHUNK_SIZE = 10;

    double GLOBAL_SIZE = 8.0;

    public Scene() {
        scene_world = new World();
    }

    public Scene(World world) {
        this.scene_world = world;
    }

    public final void loadSceneFromFile(String path) {
        this.loadPath = path;
    }

    public final void unload() {
        nodes.clear();
        isLoaded = false;
    }

    public final void load() {
        if (loadPath != null)
            new Thread(this::loadResource).start();
        else isLoaded = true;
    }

    public final void load_FILE() {
        if (loadPath != null)
            new Thread(this::loadFile).start();
        else isLoaded = true;
    }

    private void loadResource() {
        // check if file exists

        nodes.clear();

        // load all nodes from the chosen file.

        isLoaded = true;
    }

    private void loadFile() {
        // check if file exists

        nodes.clear();

        // load all nodes from the chosen file.

        isLoaded = true;
    }

    public final boolean loaded() {
        return isLoaded;
    }

    public final double getGlobalSize() {
        return GLOBAL_SIZE;
    }

    public final double setGlobalSize(double global_size) {
        return this.GLOBAL_SIZE = global_size;
    }

    public int getChunkSize() {
        return CHUNK_SIZE;
    }


    /**
     * NOT RECOMMENDED TO CHANGE DURING RUN TIME!!!
     * CHUNKS WILL GET MESSED UP AND CHUNKS WILL BE IN THE WRONG
     * PLACES!!!
     *
     * RUN ONLY BEFORE WORLD CREATION!!!
     * @param CHUNK_SIZE
     */
    public void setChunkSize(int CHUNK_SIZE) {
        this.CHUNK_SIZE = CHUNK_SIZE;
    }
}
