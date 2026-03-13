package dev.catamesh.core.builder;

import dev.catamesh.application.strategy.DiffTreeNodeStrategy;
import dev.catamesh.application.facade.DiffEngineFacade;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.HashSet;
import java.util.Set;

public class DiffEngineBuilder {

    private final Set<String> ignoredPaths = new HashSet<>();
    private DiffStrategy<DiffTreeNode> strategy;

    public DiffEngineBuilder() {
    }

    public DiffEngineBuilder exclude(String ignored) {
        ignoredPaths.add(ignored);
        return this;
    }

    public DiffEngineFacade build() {
        strategy = new DiffTreeNodeStrategy(ignoredPaths);
        return new DiffEngineFacade(this);
    }

    public Set<String> getIgnoredPaths() {
        return ignoredPaths;
    }

    public DiffStrategy<DiffTreeNode> getStrategy() {
        return strategy;
    }
}
