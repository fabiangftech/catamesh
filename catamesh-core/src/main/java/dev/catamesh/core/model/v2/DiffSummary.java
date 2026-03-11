package dev.catamesh.core.model.v2;

public final class DiffSummary {

    private int added;
    private int changed;
    private int removed;

    public int getAdded() {
        return added;
    }

    public int getChanged() {
        return changed;
    }

    public int getRemoved() {
        return removed;
    }

    public int totalChanges() {
        return added + changed + removed;
    }

    void addAdded() {
        added++;
    }

    void addChanged() {
        changed++;
    }

    void addRemoved() {
        removed++;
    }

    void merge(DiffSummary other) {
        this.added += other.added;
        this.changed += other.changed;
        this.removed += other.removed;
    }

    @Override
    public String toString() {
        return "Plan: " + added + " added, " + changed + " changed, " + removed + " removed";
    }
}