package io.github.EclProtocol.blocks.property;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class PropertyBool implements IProperty<Boolean> {
    private final String name;

    private PropertyBool(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Boolean value) {
        return value != null;
    }

    @Override
    public List<Boolean> getAllowedValues() {
        return Arrays.asList(true, false);
    }

    public static PropertyBool create(String name) {
        return new PropertyBool(name);
    }
}
