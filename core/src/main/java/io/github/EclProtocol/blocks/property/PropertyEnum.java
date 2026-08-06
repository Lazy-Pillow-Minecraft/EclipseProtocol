package io.github.EclProtocol.blocks.property;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class PropertyEnum<T extends Enum<T>> implements IProperty<T> {
    private final String name;
    private final T[] values;

    public PropertyEnum(String name, Class<T> enumClass) {
        this.name = name;
        this.values = enumClass.getEnumConstants();
    }

    @Override public String getName() { return name; }

    @Override
    public boolean isValid(T value) {
        return value != null && value.getDeclaringClass() == values[0].getDeclaringClass();
    }

    @Override
    public List<T> getAllowedValues() {
        return Arrays.asList(values);
    }
}
