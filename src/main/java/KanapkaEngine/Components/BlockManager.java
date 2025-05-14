package KanapkaEngine.Components;

import KanapkaEngine.Game.Logger;
import KanapkaEngine.Net.DataStorage.Payload;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Stores <code><strong>BlockData</strong></code> that can be accessed by the chunks and blocks.
 */
public class BlockManager {
    private static final Logger logger = new Logger("BlockManager");

    private final static Vector<BlockData> blockData;

    static {
        blockData = new Vector<>();

        createBlock(new BlockData(
                new BlockData.Builder()
                        .setRender(ResourceLoader.loadImageResource("wooden.png"))));
    }

    private BlockManager() {

    }

    /**
     * Registers a new block into the global list.
     * @param data
     */
    public static int createBlock(BlockData data) {
        if (blockData.contains(data))
            return data.getID();

        blockData.add(data);
        data.setBlockID(blockData.size() - 1);

        logger.log("Registered new block with id: " + data.getID() + ", name: " + data.blockName() + ", hasCollision: " + data.hasCollision() + ", floor: " + data.isFloor() + ", strength: " + data.blockStrength());

        return data.getID();
    }

    /**
     *
     * @param id
     * @return The block data with the corresponding id. Will throw an error if id is out of bounds.
     */
    public static BlockData getBlockData(int id) {
        return blockData.get(id);
    }

    public static int getBlockCount() {
        return blockData.size();
    }
}
