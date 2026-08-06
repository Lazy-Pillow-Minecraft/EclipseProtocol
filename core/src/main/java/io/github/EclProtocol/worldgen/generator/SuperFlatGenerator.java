package io.github.EclProtocol.worldgen.generator;

import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.worldgen.World;

@SuppressWarnings("unused")
public class SuperFlatGenerator implements WorldGenerator {
    @Override
    public void generate(World world) {
        for (int x = 0; x < world.getWorldWidth(); x++) {
            for (int z = 0; z < world.getWorldWidth(); z++) {
                world.setBlock(x, 0, z, Blocks.STONE.getDefaultState());

                for (int y = 1; y <= 3; y++) {
                    world.setBlock(x, y, z, Blocks.DIRT.getDefaultState());
                }

                world.setBlock(x, 4, z, Blocks.GRASS_BLOCK.getDefaultState());
            }
        }
    }
}
