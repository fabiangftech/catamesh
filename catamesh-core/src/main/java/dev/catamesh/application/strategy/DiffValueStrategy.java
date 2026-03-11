package dev.catamesh.application.strategy;

import dev.catamesh.core.model.v2.DiffChangeType;
import dev.catamesh.core.model.v2.DiffNodeKind;
import dev.catamesh.core.model.v2.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.Objects;

public class DiffValueStrategy implements DiffStrategy<DiffTreeNode> {
    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        if (Objects.equals(desired, current)) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.NONE, current, desired, null, null, null);
        }
        if (current == null) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.CREATE, null, desired, null, null, null);
        }

        if (desired == null) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.DELETE, current, null, null, null, null);
        }

        return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.UPDATE, current, desired, null, null, null);

    }
}
