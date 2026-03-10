package dev.catamesh.core.strategy;

import dev.catamesh.core.model.DiffChange;

import java.util.List;

public interface DiffStrategy<T> {

    List<DiffChange> compare(T desired, T current, String path);
}
