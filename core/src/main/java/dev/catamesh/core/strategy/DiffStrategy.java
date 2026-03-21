package dev.catamesh.core.strategy;

public interface DiffStrategy<T> {
    T diffNode(String path, Object desired, Object current);
}
