package io.github.EclProtocol.init;

import io.github.EclProtocol.blocks.Block;
import io.github.EclProtocol.items.Item;
import io.github.EclProtocol.registry.BlockRegistry;

public class Blocks {

    public static final Block AIR = BlockRegistry.registerWithItemSelf("air", new Block(true, 0, 0), new Item());
    public static final Block GRASS_BLOCK = BlockRegistry.registerWithItemSelf("grass_block", new Block(1), new Item());
    public static final Block DIRT = BlockRegistry.registerWithItemSelf("dirt", new Block(1), new Item());
    public static final Block STONE = BlockRegistry.registerWithItemSelf("stone", new Block(2), new Item());

    private Blocks() {}
}
