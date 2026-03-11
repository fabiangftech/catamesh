package dev.catamesh.core.model.diff;

public final class DiffChange {

    private final DiffChangeType type;
    private final String path;
    private final Object currentValue;
    private final Object desiredValue;
    private final String reason;

    public DiffChange(DiffChangeType type,
                      String path,
                      Object currentValue,
                      Object desiredValue,
                      String reason) {
        this.type = type;
        this.path = path;
        this.currentValue = currentValue;
        this.desiredValue = desiredValue;
        this.reason = reason;
    }

    public DiffChangeType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public Object getDesiredValue() {
        return desiredValue;
    }

    public String getReason() {
        return reason;
    }
}
