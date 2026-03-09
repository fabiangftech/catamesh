package dev.catamesh.core.model;


import java.util.List;

public class DiffSection {
    private final DiffScope scope;
    private final String name;
    private final List<DiffChange> changes;

    public DiffSection(DiffScope scope, String name, List<DiffChange> changes) {
        this.scope = scope;
        this.name = name;
        this.changes = List.copyOf(changes);
    }

    public DiffScope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public List<DiffChange> getChanges() {
        return changes;
    }
}
