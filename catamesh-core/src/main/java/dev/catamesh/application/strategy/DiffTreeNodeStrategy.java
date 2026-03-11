package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DiffNodeKind;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.model.DiffTypeResolver;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.Set;

public class DiffTreeNodeStrategy implements DiffStrategy<DiffTreeNode> {
    private final DiffStrategy<DiffTreeNode> diffValueStrategy = new DiffValueStrategy();
    private final DiffStrategy<DiffTreeNode> diffObjectStrategy;
    private final DiffStrategy<DiffTreeNode> diffMapStrategy;
    private final DiffStrategy<DiffTreeNode> diffListStrategy;

    public DiffTreeNodeStrategy(Set<String> ignoredPaths) {
        diffObjectStrategy = new DiffObjectStrategy(ignoredPaths, this);
        diffMapStrategy = new DiffMapStrategy(this);
        diffListStrategy = new DiffListStrategy(this);
    }

    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        DiffNodeKind kind = DiffTypeResolver.resolve(desired != null ? desired : current);
        return switch (kind) {
            case VALUE, NULL -> diffValueStrategy.diffNode(path, desired, current);
            case OBJECT -> diffObjectStrategy.diffNode(path, desired, current);
            case MAP -> diffMapStrategy.diffNode(path, desired, current);
            case LIST -> diffListStrategy.diffNode(path, desired, current);
        };
    }
}
