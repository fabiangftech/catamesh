package dev.catamesh.core.strategy;

import dev.catamesh.core.model.v2.DiffTreeNode;

public interface DiffNodeStrategy<T> {
    T diffNode(String path, Object desired, Object current);
}
