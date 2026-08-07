package io.github.EclProtocol.init;

import io.github.EclProtocol.blocks.Block;
import io.github.EclProtocol.registry.BlockRegistry;

public class Blocks {

    public static final Block AIR = BlockRegistry.registerSelf("air", new Block(true, 0, 0));
    public static final Block GRASS_BLOCK = BlockRegistry.registerSelf("grass_block", new Block(1));
    public static final Block DIRT = BlockRegistry.registerSelf("dirt", new Block(1));
    public static final Block STONE = BlockRegistry.registerSelf("stone", new Block(2));

    private Blocks() {}
}
