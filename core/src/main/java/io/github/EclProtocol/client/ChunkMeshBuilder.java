package io.github.EclProtocol.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.EclProtocol.blocks.BlockState;
import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.client.model.CubeModelDefinition;
import io.github.EclProtocol.util.math.Direction6;
import io.github.EclProtocol.worldgen.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkMeshBuilder {
    private final ModelBuilder modelBuilder = new ModelBuilder();
    private static final String ATLAS_PATH = "assets/esl_protocol/textures/atlas/blocks.pack.atlas";

    private final Map<String, MeshBuffer> buffers = new HashMap<>();

    private final World world;

    public ChunkMeshBuilder(World world) {
        this.world = world;
    }

    public ModelInstance buildChunkMesh(io.github.EclProtocol.chunks.Chunk chunk) {
        TextureAtlas atlas = new TextureAtlas(ATLAS_PATH);
        buffers.clear();
        modelBuilder.begin();
        modelBuilder.node();

        int chunkX = chunk.getChunkPos().x;
        int chunkY = chunk.getChunkPos().y;
        int chunkZ = chunk.getChunkPos().z;

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int worldX = chunkX * 16 + x;
                    int worldY = chunkY * 16 + y;
                    int worldZ = chunkZ * 16 + z;

                    BlockState state = chunk.getBlock(worldX, worldY, worldZ);
                    if (state == Blocks.AIR.getDefaultState() || state == null) continue;

                    String blockName = state.getBlock().getRegistryName();
                    CubeModelDefinition modelDef = CubeModelLoader.getModel(blockName);
                    if (modelDef == null) continue;

                    // UP
                    if (isFaceVisible(worldX, worldY + 1, worldZ)) {
                        addFace(worldX, worldY, worldZ, Direction6.UP, getTextureRegion(atlas, modelDef.up, worldX, worldY, worldZ));
                    }
                    // DOWN
                    if (isFaceVisible(worldX, worldY - 1, worldZ)) {
                        addFace(worldX, worldY, worldZ, Direction6.DOWN, getTextureRegion(atlas, modelDef.down, worldX, worldY, worldZ));
                    }
                    // NORTH (Z+1)
                    if (isFaceVisible(worldX, worldY, worldZ + 1)) {
                        addFace(worldX, worldY, worldZ, Direction6.NORTH, getTextureRegion(atlas, modelDef.north, worldX, worldY, worldZ));
                    }
                    // SOUTH (Z-1)
                    if (isFaceVisible(worldX, worldY, worldZ - 1)) {
                        addFace(worldX, worldY, worldZ, Direction6.SOUTH, getTextureRegion(atlas, modelDef.south, worldX, worldY, worldZ));
                    }
                    // WEST (X-1)
                    if (isFaceVisible(worldX - 1, worldY, worldZ)) {
                        addFace(worldX, worldY, worldZ, Direction6.WEST, getTextureRegion(atlas, modelDef.west, worldX, worldY, worldZ));
                    }
                    // EAST (X+1)
                    if (isFaceVisible(worldX + 1, worldY, worldZ)) {
                        addFace(worldX, worldY, worldZ, Direction6.EAST, getTextureRegion(atlas, modelDef.east, worldX, worldY, worldZ));
                    }
                }
            }
        }

        // 3. 构建 Mesh
        for (Map.Entry<String, MeshBuffer> entry : buffers.entrySet()) {
            MeshBuffer buffer = entry.getValue();
            if (buffer.vertexCount > 0) {
                TextureAtlas.AtlasRegion region = atlas.findRegion(entry.getKey());
                if (region == null) region = atlas.findRegion("stone");

                Material mat = new Material(TextureAttribute.createDiffuse(region));
                MeshPartBuilder partBuilder = modelBuilder.part(
                    entry.getKey(),
                    GL20.GL_TRIANGLES,
                    com.badlogic.gdx.graphics.VertexAttributes.Usage.Position |
                        com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal |
                        com.badlogic.gdx.graphics.VertexAttributes.Usage.TextureCoordinates,
                    mat
                );

                for (float[] rectData : buffer.rects) {
                    partBuilder.rect(
                        rectData[0], rectData[1], rectData[2],
                        rectData[3], rectData[4], rectData[5],
                        rectData[6], rectData[7], rectData[8],
                        rectData[9], rectData[10], rectData[11],
                        rectData[12], rectData[13], rectData[14]
                    );
                }
            }
        }

        Model model = modelBuilder.end();

        Gdx.app.log("Mesh", "Model parts: " + model.nodes.size); // 调试信息

        return new ModelInstance(model);
    }

    private boolean isFaceVisible(int worldX, int worldY, int worldZ) {
        BlockState neighbor = world.getBlock(worldX, worldY, worldZ);

        if (neighbor == null) {
            return true;
        }

        return neighbor.getBlock().ifLightTransmission();
    }

    private TextureAtlas.AtlasRegion getTextureRegion(TextureAtlas atlas, Object texObj, int x, int y, int z) {
        TextureAtlas.AtlasRegion region = null;
        if (texObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<JsonValue> variants = (List<JsonValue>) texObj;
            String path = selectWeightedTexture(variants, x, y, z);
            region = atlas.findRegion(path);
        } else if (texObj instanceof String) {
            region = atlas.findRegion((String) texObj);
        }
        return region == null ? atlas.findRegion("stone") : region;
    }

    private void addFace(int worldX, int worldY, int worldZ, Direction6 dir, TextureAtlas.AtlasRegion region) {
        if (region == null) return;
        String key = region.name;
        MeshBuffer buffer = buffers.get(key);
        if (buffer == null) {
            buffer = new MeshBuffer();
            buffers.put(key, buffer);
        }
        buffer.rects.add(calculateVertices(worldX, worldY, worldZ, dir));
        buffer.vertexCount += 4;
    }

    private float[] calculateVertices(int worldX, int worldY, int worldZ, Direction6 dir) {
        @SuppressWarnings("RedundantLocalVariable")
        float x0 = worldX;
        float x1 = worldX + 1;
        @SuppressWarnings("RedundantLocalVariable")
        float y0 = worldY;
        float y1 = worldY + 1;
        @SuppressWarnings("RedundantLocalVariable")
        float z0 = worldZ;
        float z1 = worldZ + 1;

        switch (dir) {
            case UP:    return new float[]{x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0};
            case DOWN:  return new float[]{x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0};
            case NORTH: return new float[]{x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1};
            case SOUTH: return new float[]{x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1};
            case WEST:  return new float[]{x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0};
            case EAST:  return new float[]{x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0};
            default: return new float[0];
        }
    }

    private int getSeededRandom(int x, int y, int z, int max) {
        int hash = x * 374761393 + y * 668265263 + z * 12345;
        hash = (hash ^ (hash >> 13)) * 1274126177;
        return Math.abs(hash) % max;
    }

    private String selectWeightedTexture(List<JsonValue> variants, int x, int y, int z) {
        int totalWeight = 0;
        for (JsonValue v : variants) totalWeight += v.getInt("weight", 1);
        int randomVal = getSeededRandom(x, y, z, totalWeight);
        int currentWeight = 0;
        for (JsonValue v : variants) {
            currentWeight += v.getInt("weight", 1);
            if (randomVal < currentWeight) return v.getString("path");
        }
        return variants.get(0).getString("path");
    }

    private static class MeshBuffer {
        Array<float[]> rects = new Array<>();
        int vertexCount = 0;
    }
}
