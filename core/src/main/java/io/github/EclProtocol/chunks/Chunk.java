package io.github.EclProtocol.chunks;

import io.github.EclProtocol.blocks.BlockState;
import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.util.math.Int3Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    private final BlockState AIR = Blocks.AIR.getDefaultState();
    private final short[] blocks = new short[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
    private final List<BlockState> blockList = new ArrayList<>();
    private final Map<BlockState, Integer> stateMap = new HashMap<>();
    private final Int3Pos chunkPos;
    public Chunk(Int3Pos chunkPos) {
        this.chunkPos = chunkPos;
        blockList.add(AIR);
    }

    private int getIndex(int x, int y, int z) {
        int lx = x - CHUNK_SIZE * chunkPos.x;
        int ly = y - CHUNK_SIZE * chunkPos.y;
        int lz = z - CHUNK_SIZE * chunkPos.z;
        if (lx < 0 || lx >= CHUNK_SIZE || ly < 0 || ly >= CHUNK_SIZE || lz < 0 || lz >= CHUNK_SIZE) {
            return -1;
        }
        return lx + (ly << 4) + (lz << 8);
    }

    public void setBlock(int x, int y, int z, BlockState blockState) {
        int index0 = getIndex(x, y, z);
        if (index0 == -1) return;
        if (blockState == AIR) {
            blocks[index0] = 0;
            return;
        }
        Integer index = stateMap.get(blockState);
        if (index == null) {
            index = blockList.size();
            blockList.add(blockState);
            stateMap.put(blockState, index);
        }

        blocks[index0] = index.shortValue();
    }

    public BlockState getBlock(int x, int y, int z) {
        int index = getIndex(x, y, z);
        if (index == -1) return Blocks.AIR.getDefaultState();
        return blockList.get(blocks[index]);
    }

    public Int3Pos getChunkPos() {
        return chunkPos;
    }
}
