package io.github.EclProtocol.blocks.property;
import java.util.Collection;

@SuppressWarnings("unused")
public interface IProperty<T> {
    String getName();
    boolean isValid(T value);
    Collection<T> getAllowedValues();
}
