package io.github.EclProtocol.worldgen.generator;

import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.worldgen.World;

import java.util.Random;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class SimplexTerrainGenerator implements WorldGenerator {

    private long seed;
    private final double baseFrequency = 0.007;
    private final int baseHeight = 60;
    private final int amplitude = 40;
    private final int octaves = 6;
    private final int searchRadius = 3;
    private final int blendRange = 16; // 混合区域的宽度

    private final int erosionIterations = 50000; // 模拟多少个雨滴
    private final float erosionStrength = 1f;  // 侵蚀强度

    public SimplexTerrainGenerator(long seed) {
        this.seed = seed;
    }

    @Override
    public void generate(World world) {
        int width = world.getWorldWidth();

        float[][] heightMap = new float[width][width];
        int[][] visitCount = new int[width][width];

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                double noiseValue = octaveNoise(x * baseFrequency, z * baseFrequency, octaves);
                noiseValue = (noiseValue + 1.0) / 2.0;
                heightMap[x][z] = (float) (baseHeight + noiseValue * amplitude * 2);
            }
        }

        interpolateEdges(heightMap, width);

        applyErosion(heightMap, width, erosionIterations, visitCount);

        int maxVisits = 1;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                maxVisits = Math.max(maxVisits, visitCount[x][z]);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                int height = (int) heightMap[x][z];


                for (int y = 0; y < height; y++) {
                    world.setBlock(x, y, z, Blocks.STONE.getDefaultState());
                }

                boolean isSteep = false;
                float currentHeight = heightMap[x][z];
                float maxDiff = 0;

                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        if (dx == 0 && dz == 0) continue;

                        int nx = x + dx;
                        int nz = z + dz;
                        if (nx >= 0 && nx < width && nz >= 0 && nz < width) {
                            float neighborHeight = heightMap[nx][nz];
                            float diff = Math.abs(currentHeight - neighborHeight);
                            if (diff > maxDiff) {
                                maxDiff = diff;
                            }
                        }
                    }
                }

                if (maxDiff > 2.5f) {
                    isSteep = true;
                }

                float visitDensity = (float) visitCount[x][z] / maxVisits;
                boolean isRiverBed = visitDensity > 0f;

                if (isSteep && isRiverBed) {
                    world.setBlock(x, height, z, Blocks.STONE.getDefaultState());
                } else {
                    world.setBlock(x, height, z, Blocks.GRASS_BLOCK.getDefaultState());
                    for (int i = 1; i < 3; i++) {
                        world.setBlock(x, height - i, z, Blocks.DIRT.getDefaultState());
                    }
                }
            }
        }
    }

    private void interpolateEdges(float[][] map, int width) {
        for (int i = 0; i < blendRange; i++) {
            float weight = (float) i / blendRange;

            @SuppressWarnings("RedundantLocalVariable")
            int xLeft = i;
            int xRight = width - 1 - i;
            @SuppressWarnings("RedundantLocalVariable")
            int zTop = i;
            int zBottom = width - 1 - i;

            int baseOffset = blendRange;
            @SuppressWarnings("RedundantLocalVariable")
            int xLeftBase = baseOffset;
            int xRightBase = width - 1 - baseOffset;
            @SuppressWarnings("RedundantLocalVariable")
            int zTopBase = baseOffset;
            int zBottomBase = width - 1 - baseOffset;

            for (int z = 0; z < width; z++) {
                float leftBase = map[xLeftBase][z];
                float rightBase = map[xRightBase][z];
                float stitchHeight = (leftBase + rightBase) / 2.0f;

                float rawLeft = map[xLeft][z];
                float rawRight = map[xRight][z];

                map[xLeft][z] = stitchHeight * (1 - weight) + rawLeft * weight;
                map[xRight][z] = stitchHeight * (1 - weight) + rawRight * weight;
            }

            for (int x = 0; x < width; x++) {
                float topBase = map[x][zTopBase];
                float bottomBase = map[x][zBottomBase];
                float stitchHeight = (topBase + bottomBase) / 2.0f;

                float rawTop = map[x][zTop];
                float rawBottom = map[x][zBottom];

                map[x][zTop] = stitchHeight * (1 - weight) + rawTop * weight;
                map[x][zBottom] = stitchHeight * (1 - weight) + rawBottom * weight;
            }
        }

        float cornerAvg = (map[blendRange][blendRange] +
            map[blendRange][width - 1 - blendRange] +
            map[width - 1 - blendRange][blendRange] +
            map[width - 1 - blendRange][width - 1 - blendRange]) / 4.0f;

        map[0][0] = cornerAvg;
        map[0][width - 1] = cornerAvg;
        map[width - 1][0] = cornerAvg;
        map[width - 1][width - 1] = cornerAvg;
    }

    @SuppressWarnings("SameParameterValue")
    private void applyErosion(float[][] map, int width, int iterations, int[][] visitCount) {
        Random random = new Random(seed);
        Random deterministicRandom = new Random();

        for (int i = 0; i < iterations; i++) {
            deterministicRandom.setSeed(seed + i);
            int x = random.nextInt(width);
            int z = random.nextInt(width);

            float sediment = 0;

            for (int j = 0; j < 100; j++) {
                visitCount[x][z]++;
                float heightDiffX = 0;
                float heightDiffZ = 0;
                float currentHeight = map[x][z];

                for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                    for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        int nx = (x + dx + width) % width;
                        int nz = (z + dz + width) % width;

                        float diff = map[nx][nz] - currentHeight;

                        float distance = (float) Math.sqrt(dx * dx + dz * dz);
                        float weight = 1.0f / distance;

                        if (diff < 0) {
                            heightDiffX += dx * diff * weight;
                            heightDiffZ += dz * diff * weight;
                        }
                    }
                }

                float inertia = 0.2f;
                heightDiffX += (deterministicRandom.nextFloat() - 0.5f) * inertia;
                heightDiffZ += (deterministicRandom.nextFloat() - 0.5f) * inertia;

                if (heightDiffX == 0 && heightDiffZ == 0) {
                    break;
                }

                int nextX = x + (heightDiffX < 0 ? -1 : 0) + (heightDiffX > 0 ? 1 : 0);
                int nextZ = z + (heightDiffZ < 0 ? -1 : 0) + (heightDiffZ > 0 ? 1 : 0);
                nextX = (nextX + width) % width;
                nextZ = (nextZ + width) % width;

                float targetHeight = map[nextX][nextZ];
                float slope = currentHeight - targetHeight;

                float erosionAmount = slope * erosionStrength * 0.5f;

                if (erosionAmount > 0) {
                    map[x][z] -= erosionAmount;
                    sediment += erosionAmount;
                }

                if (sediment > 0) {
                    float depositAmount = sediment * 0.3f;

                    boolean deposited = false;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            int nx = (x + dx + width) % width;
                            int nz = (z + dz + width) % width;

                            if (map[nx][nz] < map[x][z]) {
                                map[nx][nz] += depositAmount * 0.5f;
                                deposited = true;
                            }
                        }
                    }

                    if (deposited) {
                        sediment -= depositAmount;
                    }
                }

                x = nextX;
                z = nextZ;
            }

            if (sediment > 0) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        int nx = (x + dx + width) % width;
                        int nz = (z + dz + width) % width;
                        map[nx][nz] += sediment * 0.11f;
                    }
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private double octaveNoise(double x, double z, int octaves) {
        double total = 0;
        double frequency = 1.0;
        double amplitude = 1.0;

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, z * frequency) * amplitude;

            frequency *= 2.0;
            amplitude *= 0.5;
        }

        return total;
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
