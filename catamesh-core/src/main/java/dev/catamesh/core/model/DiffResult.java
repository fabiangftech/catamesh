package dev.catamesh.core.model;

public final class DiffResult {

    private final DiffTreeNode root;

    public DiffResult(DiffTreeNode root) {
        this.root = root;
    }

    public DiffTreeNode getRoot() {
        return root;
    }

    public DiffSummary getSummary() {
        return root.computeSummary();
    }

    public boolean hasChanges() {
        return getSummary().totalChanges() > 0;
    }
}