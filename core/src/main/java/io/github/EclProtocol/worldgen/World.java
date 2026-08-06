package io.github.EclProtocol.worldgen;

import io.github.EclProtocol.blocks.BlockState;
import io.github.EclProtocol.chunks.Chunk;
import io.github.EclProtocol.chunks.ChunkColumn;
import io.github.EclProtocol.util.math.Int2Pos;
import io.github.EclProtocol.worldgen.generator.WorldGenerator;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class World {
    private final int worldSize;
    private final int worldHeight;
    private final Map<Int2Pos, ChunkColumn> chunkColumnMap = new HashMap<>();
    private final long seed;
    private final long worldTime;
    public World(int worldSize, int worldHeight, long seed, WorldGenerator generator) {
        this.worldSize = worldSize;
        this.worldHeight = worldHeight;
        this.seed = seed;
        this.worldTime = 0;

        long start = System.currentTimeMillis();

        for (int cx = 0; cx < worldSize; cx++) {
            for (int cz = 0; cz < worldSize; cz++) {
                chunkColumnMap.put(new Int2Pos(cx, cz), new ChunkColumn(worldHeight, new Int2Pos(cx, cz)));
            }
        }

        generator.generate(this);
    }

    public long getWorldTime() {
        return worldTime;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public int getWorldWidth() {
        return worldSize * 16;
    }

    public int getWorldChunkHeight() {
        return worldHeight;
    }

    public int getWorldHeight() {
        return worldHeight * 16;
    }

    public long getSeed() {
        return seed;
    }

    public Map<Int2Pos, ChunkColumn> getChunkColumnMap() {
        return chunkColumnMap;
    }

    public BlockState getBlock(int x, int y, int z) {
        x = loopCoord(x, getWorldWidth());
        y = loopCoord(y, getWorldHeight());
        z = loopCoord(z, getWorldWidth());
        return getChunk(x, y, z).getBlock(x, y, z);
    }

    public void setBlock(int x, int y, int z, BlockState blockState) {
        x = loopCoord(x, getWorldWidth());
        y = loopCoord(y, getWorldHeight());
        z = loopCoord(z, getWorldWidth());
        getChunk(x, y, z).setBlock(x, y, z, blockState);
    }

    private Chunk getChunk(int x, int y, int z) {
        x = loopCoord(x, getWorldWidth());
        y = loopCoord(y, getWorldHeight());
        z = loopCoord(z, getWorldWidth());
        return chunkColumnMap.get(new Int2Pos(x / 16, z / 16)).getChunk(y / 16);
    }

    private int loopCoord(int value, int range) {
        return (value % range + range) % range;
    }
}
