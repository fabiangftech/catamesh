package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DiffChangeType;
import dev.catamesh.core.model.DiffNodeKind;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.Objects;

public class DiffValueStrategy implements DiffStrategy<DiffTreeNode> {
    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        if (desired == null && current == null) {
            return new DiffTreeNode(path, DiffNodeKind.NULL, DiffChangeType.NONE, null, null, null, null, null);
        }
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
