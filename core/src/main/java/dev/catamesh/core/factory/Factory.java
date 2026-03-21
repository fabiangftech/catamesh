package dev.catamesh.core.factory;

public interface Factory<I, O> {
    O create(I input);

    default O create() {
        return create(null);
    }
}
