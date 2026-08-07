package io.github.EclProtocol.client;

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
import io.github.EclProtocol.client.model.CubeModelDefinition;
import io.github.EclProtocol.config.Constants;
import io.github.EclProtocol.init.Blocks;
import io.github.EclProtocol.util.GameID;
import io.github.EclProtocol.util.math.Direction6;
import io.github.EclProtocol.util.math.Int2Pos;
import io.github.EclProtocol.worldgen.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkMeshBuilder {
    private static final String ATLAS_PATH = "assets/" + Constants.NAME_SPACE + "/textures/atlas/blocks.pack.atlas";
    private final ModelBuilder modelBuilder = new ModelBuilder();
    private final TextureAtlas atlas;
    private final World world;

    public ChunkMeshBuilder(World world) {
        this.world = world;
        this.atlas = new TextureAtlas(ATLAS_PATH);
    }
    public interface OverlayResolver {
        /**
         * @param worldX 世界坐標 X
         * @param worldY 世界坐標 Y
         * @param worldZ 世界坐標 Z
         * @param dir 當前面方向
         * @return Overlay 紋理的名稱 (例如 "overlay/overlay_a_a")，如果返回 null 則表示不疊加
         */
        String resolveOverlay(int worldX, int worldY, int worldZ, Direction6 dir);
    }


    public ChunkMeshData buildChunkMeshData(io.github.EclProtocol.chunks.Chunk chunk, OverlayResolver resolver) {
        ChunkMeshData data = new ChunkMeshData();
        data.chunkPos = new Int2Pos(chunk.getChunkPos().x, chunk.getChunkPos().z);

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

                    GameID blockName = state.getBlock().getRegistryName();
                    CubeModelDefinition modelDef = CubeModelLoader.getModel(blockName);
                    if (modelDef == null) continue;

                    if (isFaceVisible(worldX, worldY + 1, worldZ)) {
                        String baseTex = getTextureName(modelDef.up, worldX, worldY, worldZ);

                        String overlayTex = (resolver != null) ? resolver.resolveOverlay(worldX, worldY, worldZ, Direction6.UP) : null;

                        if (overlayTex != null) {
                            String key = baseTex + "###" + overlayTex;
                            addFaceToBuffer(data.overlayBuffers, key, worldX, worldY, worldZ, Direction6.UP);
                        } else {
                            addFaceToData(data, worldX, worldY, worldZ, Direction6.UP, baseTex);
                        }
                    }
                    // DOWN
                    if (isFaceVisible(worldX, worldY - 1, worldZ)) {
                        addFaceToData(data, worldX, worldY, worldZ, Direction6.DOWN, getTextureName(modelDef.down, worldX, worldY, worldZ));
                    }
                    // NORTH
                    if (isFaceVisible(worldX, worldY, worldZ + 1)) {
                        addFaceToData(data, worldX, worldY, worldZ, Direction6.NORTH, getTextureName(modelDef.north, worldX, worldY, worldZ));
                    }
                    // SOUTH
                    if (isFaceVisible(worldX, worldY, worldZ - 1)) {
                        addFaceToData(data, worldX, worldY, worldZ, Direction6.SOUTH, getTextureName(modelDef.south, worldX, worldY, worldZ));
                    }
                    // WEST
                    if (isFaceVisible(worldX - 1, worldY, worldZ)) {
                        addFaceToData(data, worldX, worldY, worldZ, Direction6.WEST, getTextureName(modelDef.west, worldX, worldY, worldZ));
                    }
                    // EAST
                    if (isFaceVisible(worldX + 1, worldY, worldZ)) {
                        addFaceToData(data, worldX, worldY, worldZ, Direction6.EAST, getTextureName(modelDef.east, worldX, worldY, worldZ));
                    }
                }
            }
        }
        return data;
    }

    @SuppressWarnings("SameParameterValue")
    private void addFaceToBuffer(Map<String, Array<float[]>> targetMap, String key, int worldX, int worldY, int worldZ, Direction6 dir) {
        Array<float[]> buffer = targetMap.get(key);
        if (buffer == null) {
            buffer = new Array<>();
            targetMap.put(key, buffer);
        }
        buffer.add(calculateVertices(worldX, worldY, worldZ, dir));
    }

    public ModelInstance createModelFromData(ChunkMeshData data) {
        modelBuilder.begin();
        modelBuilder.node();

        for (Map.Entry<String, Array<float[]>> entry : data.buffers.entrySet()) {
            createMeshPart(entry.getKey(), entry.getValue(), null);
        }

        for (Map.Entry<String, Array<float[]>> entry : data.overlayBuffers.entrySet()) {
            String key = entry.getKey();

            if (key.contains("###")) {
                String[] parts = key.split("###");
                String baseTexName = parts[0];
                String overlayTexName = parts[1];

                createMeshPart(key, entry.getValue(), new String[]{baseTexName, overlayTexName});
            } else {
                createMeshPart(key, entry.getValue(), null);
            }
        }

        Model model = modelBuilder.end();
        ModelInstance instance = new ModelInstance(model);
        instance.userData = data.chunkPos;
        return instance;
    }

    private void createMeshPart(String partID, Array<float[]> rects, String[] textures) {
        if (rects.size == 0) return;

        TextureAtlas.AtlasRegion baseRegion;
        Material mat = new Material();

        if (textures == null) {
            baseRegion = atlas.findRegion(partID);
            if (baseRegion == null) baseRegion = atlas.findRegion("stone");
            mat.set(TextureAttribute.createDiffuse(baseRegion));
        } else {
            String baseTexName = textures[0];
            String overlayTexName = textures[1];

            baseRegion = atlas.findRegion(baseTexName);
            if (baseRegion == null) baseRegion = atlas.findRegion("stone");

            TextureAtlas.AtlasRegion overlayRegion = atlas.findRegion(overlayTexName);
            mat.set(TextureAttribute.createDiffuse(baseRegion));
            if (overlayRegion != null) {
                mat.set(new TextureAttribute(TextureAttribute.Emissive, overlayRegion));
            }
        }

        MeshPartBuilder partBuilder = modelBuilder.part(
            partID,
            GL20.GL_TRIANGLES,
            com.badlogic.gdx.graphics.VertexAttributes.Usage.Position |
                com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal |
                com.badlogic.gdx.graphics.VertexAttributes.Usage.TextureCoordinates,
            mat
        );

        for (int i = 0; i < rects.size; i++) {
            float[] rectData = rects.get(i);
            partBuilder.rect(
                rectData[0], rectData[1], rectData[2],
                rectData[3], rectData[4], rectData[5],
                rectData[6], rectData[7], rectData[8],
                rectData[9], rectData[10], rectData[11],
                rectData[12], rectData[13], rectData[14]
            );
        }
    }


    private boolean isFaceVisible(int worldX, int worldY, int worldZ) {
        BlockState neighbor = world.getBlock(worldX, worldY, worldZ);
        if (neighbor == null) {
            return true;
        }
        return neighbor.getBlock().ifLightTransmission();
    }

    private String getTextureName(Object texObj, int x, int y, int z) {
        if (texObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<JsonValue> variants = (List<JsonValue>) texObj;
            return selectWeightedTexture(variants, x, y, z);
        } else if (texObj instanceof String) {
            return (String) texObj;
        }
        return "stone";
    }

    private void addFaceToData(ChunkMeshData data, int worldX, int worldY, int worldZ, Direction6 dir, String textureName) {
        if (textureName == null) textureName = "stone";

        Array<float[]> buffer = data.buffers.get(textureName);
        if (buffer == null) {
            buffer = new Array<>();
            data.buffers.put(textureName, buffer);
        }
        buffer.add(calculateVertices(worldX, worldY, worldZ, dir));
    }

    private float[] calculateVertices(int worldX, int worldY, int worldZ, Direction6 dir) {
        @SuppressWarnings("RedundantLocalVariable") float x0 = worldX;
        float x1 = worldX + 1;
        @SuppressWarnings("RedundantLocalVariable") float y0 = worldY;
        float y1 = worldY + 1;
        @SuppressWarnings("RedundantLocalVariable") float z0 = worldZ;
        float z1 = worldZ + 1;
        switch (dir) {
            case UP:
                return new float[]{x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0};
            case DOWN:
                return new float[]{x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0};
            case NORTH:
                return new float[]{x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1};
            case SOUTH:
                return new float[]{x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1};
            case WEST:
                return new float[]{x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0};
            case EAST:
                return new float[]{x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0};
            default:
                return new float[0];
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
        return variants.getFirst().getString("path");
    }

    public static class ChunkMeshData {
        public Int2Pos chunkPos;
        public Map<String, Array<float[]>> buffers = new HashMap<>();
        public Map<String, Array<float[]>> overlayBuffers = new HashMap<>();
    }
}
