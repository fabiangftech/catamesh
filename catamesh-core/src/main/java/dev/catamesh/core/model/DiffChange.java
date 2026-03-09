package dev.catamesh.core.model;

public class DiffChange {
    private final DiffOp op;
    private final String path;
    private final Object current;
    private final Object desired;

    public DiffChange(DiffOp op, String path, Object current, Object desired) {
        this.op = op;
        this.path = path;
        this.current = current;
        this.desired = desired;
    }

    public DiffOp getOp() {
        return op;
    }

    public String getPath() {
        return path;
    }

    public Object getCurrent() {
        return current;
    }

    public Object getDesired() {
        return desired;
    }

    public static DiffChange add(String path, Object desired) {
        return new DiffChange(DiffOp.ADD, path, null, desired);
    }

    public static DiffChange remove(String path, Object current) {
        return new DiffChange(DiffOp.REMOVE, path, current, null);
    }

    public static DiffChange replace(String path, Object current, Object desired) {
        return new DiffChange(DiffOp.REPLACE, path, current, desired);
    }
}
