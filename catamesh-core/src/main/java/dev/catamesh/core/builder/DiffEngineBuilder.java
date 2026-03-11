package dev.catamesh.core.builder;

import dev.catamesh.core.model.v2.DiffEngine;

import java.util.HashSet;
import java.util.Set;

public class DiffEngineBuilder {

    private final Set<String> ignoredPaths = new HashSet<>();

    public DiffEngineBuilder() {
    }

    public DiffEngineBuilder exclude(String ignored) {
        ignoredPaths.add(ignored);
        return this;
    }

    public DiffEngine build() {
        return new DiffEngine(this);
    }

    public Set<String> getIgnoredPaths() {
        return ignoredPaths;
    }
}
