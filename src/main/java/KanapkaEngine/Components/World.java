package KanapkaEngine.Components;

import KanapkaEngine.Game.Scene;
import KanapkaEngine.Game.SceneManager;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class World {
    private HashMap<Integer, HashMap<Integer, Chunk>> chunk_map = new HashMap<>();

    private String worldName = "NewWorld";

    private HashMap<Integer, HashMap<Integer, Boolean>> registeredRegions = new HashMap<>();

    public final Chunk get(int x, int y) {
        onGet(new Point(x, y));
        if (chunk_map.containsKey(x))
            return chunk_map.get(x).get(y);
        else return null;
    }

    public final boolean hasChunk(int x, int y) {
        if (chunk_map.containsKey(x))
            return chunk_map.get(x).containsKey(y);
        else return false;
    }

    public final void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void onGet(Point p) {

    }

    public final void set(Chunk chunk) {
        Objects.requireNonNull(chunk, "Chunk cannot be null.");
        if (chunk.getParent() == this) {
            if (!chunk_map.containsKey(chunk.getPoint().x))
                chunk_map.put(chunk.getPoint().x, new HashMap<>());
            chunk_map.get(chunk.getPoint().x).put(chunk.getPoint().y, chunk);
            Point r = getRegionPoint(chunk.getPoint());
            activateRegion(r.x, r.y);
            onSet(chunk);
        }
    }

    /**
     * @param position
     * @return World position region
     */
    public static Point getRegionPoint(Vector2D position) {
        double chunkSize = (Chunk.BLOCK_SCALE * SceneManager.getCurrentlyLoaded().getChunkSize());
        return new Point((int) Math.floor(position.getX() / chunkSize / 32.0), (int) Math.floor(position.getY() / chunkSize / 32.0));
    }

    /**
     * @param chunkPoint
     * @return Chunk point region
     */
    public static Point getRegionPoint(Point chunkPoint) {
        return new Point((int) Math.floor(chunkPoint.x / 32.0), (int) Math.floor(chunkPoint.y / 32.0));
    }

    public void onSet(Chunk chunk) {

    }

    public static World getCurrent() {
        if (!SceneManager.hasScene())
            return null;
        return SceneManager.getCurrentlyLoaded().scene_world;
    }

    public final void saveRegion(int x, int y) {
        if (hasRegion(x, y)) {
            File worldDir = new File(worldName);

            if (!worldDir.exists() || worldDir.isFile())
                worldDir.mkdir();

            List<byte[]> chunkData = new ArrayList<>();

            int chunkByteCount = 0;
            int chunkCount = 0;

            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    Point p = new Point(x * 32 + i, y * 32 + j);
                    Chunk chunk = get(p.x, p.y);
                    if (chunk != null) {
                        byte[] data = chunk.getSave();
                        chunkByteCount += data.length;
                        chunkData.add(data);
                        chunkCount++;
                    }
                }
            }

            ByteBuffer buffer = ByteBuffer.allocate(4 + chunkByteCount);
            buffer.putInt(chunkCount);
            for (byte[] data : chunkData) {
                buffer.put(data);
            }

            Path path = Paths.get(worldName + "/rx"+x+"y"+y);
            try {
                Files.write(path, buffer.array());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final void saveWorld() {
        for (int x : registeredRegions.keySet()) {
            for (int y : registeredRegions.get(x).keySet()) {
                saveRegion(x, y);
            }
        }
        System.out.println("Saved world.");
    }

    public final boolean hasRegion(int x, int y) {
         if (registeredRegions.containsKey(x))
             return registeredRegions.get(x).containsKey(y);
         return false;
    }

    private void activateRegion(int x, int y) {
        if (!registeredRegions.containsKey(x))
            registeredRegions.put(x, new HashMap<>());
        registeredRegions.get(x).put(y, true);
    }

    public static World load(World to, String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            to.setWorldName(dir.getName());
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().startsWith("r")) {
                    String coords = file.getName().substring(1, file.getName().length() - 1);
                    boolean s = false;
                    StringBuilder x = new StringBuilder();
                    StringBuilder y = new StringBuilder();
                    for (int i = 0; i < coords.length(); i++) {
                        char c = coords.charAt(i);
                        if (c == 'x')
                            s = false;
                        else if (c == 'y')
                            s = true;
                        else if (Character.isDigit(c) || c == '-') {
                            if (s)
                                y.append(c);
                            else
                                x.append(c);
                        }
                    }
                    int _x = Integer.getInteger(x.toString());
                    int _y = Integer.getInteger(y.toString());

                    try {
                        ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(file.toPath()));

                        int chunkCount = buffer.getInt();

                        for (int i = 0; i < chunkCount; i++) {
                            int chunk_x = buffer.getInt(); 
                            int chunk_y = buffer.getInt();
                            int blockCount = buffer.getInt();

                            byte[] blockData = new byte[blockCount];
                            buffer.get(blockData);

                            load(to, new Point(chunk_x, chunk_y), blockCount, blockData);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private static Chunk load(World world, Point pos, int blockCount, byte[] data) {
        Chunk chunk = Chunk.build(pos, world);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        for (int i = 0; i < blockCount; i++) {
            byte x = buffer.get();
            byte y = buffer.get();
            int id = buffer.getInt();

            new Block(chunk, new Point(x, y), id).special_id = buffer.getInt();
        }
        return chunk;
    }
}
