package io.github.EclProtocol.init;

import io.github.EclProtocol.blocks.Block;
import io.github.EclProtocol.registry.BlockRegistry;

import static io.github.EclProtocol.util.GameID.createID;

public class Blocks {

    public static final Block AIR = BlockRegistry.register(createID("air"), new Block(true, 0, 0));
    public static final Block GRASS_BLOCK = BlockRegistry.register(createID("grass_block"), new Block(1));
    public static final Block DIRT = BlockRegistry.register(createID("dirt"), new Block(1));
    public static final Block STONE = BlockRegistry.register(createID("stone"), new Block(2));

    private Blocks() {}
}
