package io.github.EclProtocol.blocks;

import io.github.EclProtocol.blocks.property.IProperty;
import io.github.EclProtocol.blocks.property.PropertyBool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class BlockState {
    private final Block block;
    private final Map<IProperty<?>, Comparable<?>> storage;

    BlockState(Block block, Map<IProperty<?>, Comparable<?>> properties) {
        this.block = block;
        this.storage = Collections.unmodifiableMap(new HashMap<>(properties));
    }

    protected BlockState(Block block) {
        this.block = block;
        this.storage = Collections.emptyMap();
    }

    // Getter
    public Block getBlock() {
        return this.block;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        return (T) storage.get(property);
    }

    public <T extends Comparable<T>> T getValue(IProperty<T> property, T defaultValue) {
        T val = getValue(property);
        return val != null ? val : defaultValue;
    }

    public boolean getValue(PropertyBool property, boolean defaultValue) {
        Boolean val = getValue(property);
        return val != null ? val : defaultValue;
    }

    public <T extends Comparable<T>> BlockState withProperty(IProperty<T> property, T value) {
        if (!block.getProperties().contains(property)) {
            throw new IllegalArgumentException("方块不包含属性: " + property.getName());
        }
        if (!property.isValid(value)) {
            throw new IllegalArgumentException("值无效: " + value);
        }

        Map<IProperty<?>, Comparable<?>> newMap = new HashMap<>(this.storage);
        newMap.put(property, value);

        return new BlockState(this.block, newMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockState that = (BlockState) o;
        if (!Objects.equals(block, that.block)) return false;
        return Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, storage);
    }

    @Override
    public String toString() {
        return block.getRegistryName() + storage;
    }
}
