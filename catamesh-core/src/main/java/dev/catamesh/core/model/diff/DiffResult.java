package dev.catamesh.core.model.diff;

import java.util.List;

public final class DiffResult {

    private final List<DiffChange> changes;

    public DiffResult(List<DiffChange> changes) {
        this.changes = List.copyOf(changes);
    }

    public List<DiffChange> getChanges() {
        return changes;
    }

    public boolean hasChanges() {
        return !changes.isEmpty();
    }
}
