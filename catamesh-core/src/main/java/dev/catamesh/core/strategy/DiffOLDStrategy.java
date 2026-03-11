package dev.catamesh.core.strategy;

import dev.catamesh.core.model.DiffChange;

import java.util.List;

@Deprecated
public interface DiffOLDStrategy<T> {

    List<DiffChange> compare(T desired, T current, String path);
}
