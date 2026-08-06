package io.github.EclProtocol.worldgen.generator;

import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.worldgen.World;

import java.util.Random;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class FlatTerrainGenerator implements WorldGenerator {

    private long seed;
    private final double baseFrequency = 0.007;
    private final int baseHeight = 60;
    private final int amplitude = 30;
    private final int stepHeight = 4;
    private final int octaves = 3;

    public FlatTerrainGenerator(long seed) {
        this.seed = seed;
    }

    @Override
    public void generate(World world) {
        int width = world.getWorldWidth();

        float[][] heightMap = new float[width][width];

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                double noiseValue = octaveNoise(x * baseFrequency, z * baseFrequency, octaves);
                noiseValue = (noiseValue + 1.0) / 2.0;
                heightMap[x][z] = baseHeight + stepHeight * (int) (noiseValue * amplitude / stepHeight * 2);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                int height = (int) heightMap[x][z];


                for (int y = 0; y < height; y++) {
                    world.setBlock(x, y, z, Blocks.STONE.getDefaultState());
                }

                world.setBlock(x, height, z, Blocks.GRASS_BLOCK.getDefaultState());
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private double octaveNoise(double x, double z, int octaves) {
        double total = 0;
        double frequency = 1.0; // 频率倍增器
        double amplitude = 1.0; // 振幅衰减器
        double maxValue = 0;    // 用于归一化

        for (int i = 0; i < octaves; i++) {
            // 叠加每一层的噪声值
            // 注意：这里把 frequency 传给了底层的 noise 函数
            total += noise(x * frequency, z * frequency) * amplitude;

            maxValue += amplitude;

            // 频率翻倍，振幅减半 (Persistence = 0.5)
            frequency *= 2.0;
            amplitude *= 0.5;
        }

        // 归一化到 -1 到 1 之间 (大致范围)
        return total; // 也可以 return total / maxValue;
    }

    private final int[] perm = new int[512];
    {
        for (int i = 0; i < 256; i++) {
            perm[i] = i;
        }
        Random rand = new Random(seed);
        for (int i = 0; i < 256; i++) {
            int j = rand.nextInt(256);
            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
            perm[i + 256] = perm[i];
        }
    }

    private double noise(double x, double z) {
        int X = (int) Math.floor(x) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(z);

        int A  = perm[X] + Z;
        int AA = perm[A];
        int AB = perm[A + 1];
        int B  = perm[X + 1] + Z;
        int BA = perm[B];
        int BB = perm[B + 1];

        return lerp(v,
            lerp(u, grad(perm[AA], x, z),     grad(perm[BA], x - 1, z)),
            lerp(u, grad(perm[AB], x, z - 1), grad(perm[BB], x - 1, z - 1))
        );
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double z) {
        int h = hash & 3;
        double u = h < 2 ? x : z;
        double v = h < 2 ? z : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
