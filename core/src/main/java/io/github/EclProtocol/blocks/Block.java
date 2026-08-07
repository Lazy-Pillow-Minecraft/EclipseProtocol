package io.github.EclProtocol.blocks;

import io.github.EclProtocol.blocks.property.IProperty;
import io.github.EclProtocol.util.GameID;

import java.util.*;

@SuppressWarnings("unused")
public class Block {
    private final boolean ifLightTransmission;
    private final int hardness;
    private final int light;
    private final int lightAttenuation;
    private final Map<Map<IProperty<?>, Comparable<?>>, BlockState> stateCache = new HashMap<>();
    private BlockState defaultState;
    private final List<IProperty<?>> properties = new ArrayList<>();
    private final BlockRenderType blockRenderType;

    private GameID registryName = null;
    private int intId = -1;

    public Block(boolean ifLightTransmission, int hardness, int light, int lightAttenuation){
        this.ifLightTransmission = ifLightTransmission;
        this.hardness = hardness;
        this.light = light;
        this.lightAttenuation = lightAttenuation;
        this.blockRenderType = BlockRenderType.Cube;
    }
    public Block(int hardness, int light){
        this.ifLightTransmission = false;
        this.hardness = hardness;
        this.light = light;
        this.lightAttenuation = 0;
        this.blockRenderType = BlockRenderType.Cube;
    }
    public Block(boolean ifLightTransmission, int hardness, int lightAttenuation){
        this.ifLightTransmission = ifLightTransmission;
        this.hardness = hardness;
        this.light = 0;
        this.lightAttenuation = lightAttenuation;
        this.blockRenderType = BlockRenderType.Cube;
    }
    public Block(int hardness){
        this.ifLightTransmission = false;
        this.hardness = hardness;
        this.light = 0;
        this.lightAttenuation = 0;
        this.blockRenderType = BlockRenderType.Cube;
    }
    public Block(boolean ifLightTransmission, int hardness, int light, int lightAttenuation, BlockRenderType blockRenderType){
        this.ifLightTransmission = ifLightTransmission;
        this.hardness = hardness;
        this.light = light;
        this.lightAttenuation = lightAttenuation;
        this.blockRenderType = blockRenderType;
    }
    public Block(int hardness, int light, BlockRenderType blockRenderType){
        this.ifLightTransmission = false;
        this.hardness = hardness;
        this.light = light;
        this.lightAttenuation = 0;
        this.blockRenderType = blockRenderType;
    }
    public Block(boolean ifLightTransmission, int hardness, int lightAttenuation, BlockRenderType blockRenderType){
        this.ifLightTransmission = ifLightTransmission;
        this.hardness = hardness;
        this.light = 0;
        this.lightAttenuation = lightAttenuation;
        this.blockRenderType = blockRenderType;
    }
    public Block(int hardness, BlockRenderType blockRenderType){
        this.ifLightTransmission = false;
        this.hardness = hardness;
        this.light = 0;
        this.lightAttenuation = 0;
        this.blockRenderType = blockRenderType;
    }

    protected <T extends IProperty<?>> T createProperty(T property) {
        this.properties.add(property);
        return property;
    }

    public void setRegistryId(GameID name, int id) {
        if (this.registryName != null) {
            throw new IllegalStateException("方块 " + this + " 已经注册过了，不能重复注册！");
        }
        this.registryName = name;
        this.intId = id;
    }

    public BlockState getState(Map<IProperty<?>, Comparable<?>> properties) {
        return stateCache.computeIfAbsent(properties, p -> BlockState.of(this, new HashMap<>(p)));
    }

    public BlockState getDefaultState() {
        if (defaultState == null) {
            defaultState = getState(Collections.emptyMap());
        }
        return defaultState;
    }

    // Getter
    public GameID getRegistryName() {
        return registryName;
    }

    public String getStringName() {
        return (registryName.getNameSpace() + registryName.getId());
    }

    public int getIntId() {
        return intId;
    }

    public List<IProperty<?>> getProperties() {
        return properties;
    }

    public boolean getIfLightTransmission() {
        return ifLightTransmission;
    }

    public int getHardness() {
        return hardness;
    }

    public int getLight() {
        return light;
    }

    public int getLightAttenuation() {
        return lightAttenuation;
    }

    //bl
    public boolean ifLightTransmission() {
        return ifLightTransmission;
    }
}
