package dev.catamesh.application.facade;


import dev.catamesh.core.builder.DiffEngineBuilder;
import dev.catamesh.core.facade.DiffFacade;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

public class DiffEngineFacade implements DiffFacade {

    private static final String ROOT_PATH = "";
    private final DiffStrategy<DiffTreeNode> strategy;

    public DiffEngineFacade(DiffEngineBuilder builder) {
        this.strategy = builder.getStrategy();
    }

    @Override
    public DiffResult compare(Object desired, Object current) {

        DiffTreeNode root = strategy.diffNode(ROOT_PATH, desired, current);

        return new DiffResult(root);
    }

    public static DiffEngineBuilder builder() {
        return new DiffEngineBuilder();
    }
}

