package dev.catamesh.core.model;

public class ApplySummary {
    private final int executed;
    private final int skipped;
    private final int failed;

    public ApplySummary(int executed, int skipped, int failed) {
        this.executed = executed;
        this.skipped = skipped;
        this.failed = failed;
    }

    public int getExecuted() {
        return executed;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getFailed() {
        return failed;
    }
}
