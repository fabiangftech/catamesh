package dev.catamesh.application.strategy;

import dev.catamesh.core.model.v2.DiffNodeKind;
import dev.catamesh.core.model.v2.DiffTreeNode;
import dev.catamesh.core.model.v2.DiffTypeResolver;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.Set;

public class DiffTreeNodeStrategy implements DiffStrategy<DiffTreeNode> {
    private final DiffStrategy<DiffTreeNode> diffValueStrategy = new DiffValueStrategy();
    private final DiffStrategy<DiffTreeNode> diffObjectStrategy;
    private final DiffStrategy<DiffTreeNode> diffMapStrategy = new DiffMapStrategy();
    private final DiffStrategy<DiffTreeNode> diffListStrategy = new DiffListStrategy();

    public DiffTreeNodeStrategy(Set<String> ignoredPaths) {
        diffObjectStrategy = new DiffObjectStrategy(ignoredPaths);
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
