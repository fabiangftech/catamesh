package dev.catamesh.core.model.v2;


import dev.catamesh.core.builder.DiffEngineBuilder;
import dev.catamesh.core.strategy.DiffStrategy;

public class DiffEngine {
    private DiffStrategy<DiffTreeNode> strategy;

    public DiffEngine() {
    }

    public DiffEngine(DiffEngineBuilder builder) {
        this.strategy = builder.getStrategy();
    }

    public DiffResult compare(Object desired, Object current) {

        DiffTreeNode root = strategy.diffNode("", desired, current);

        return new DiffResult(root);
    }

    public static DiffEngineBuilder builder() {
        return new DiffEngineBuilder();
    }
}

