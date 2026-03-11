package dev.catamesh.core.model;

@Deprecated
public class DiffSummary {
    private int add;
    private int remove;
    private int replace;

    public DiffSummary() {
        // do nothing
    }

    public int getAdd() {
        return add;
    }

    public int getRemove() {
        return remove;
    }

    public int getReplace() {
        return replace;
    }

    public void plus(DiffOp op) {
        switch (op) {
            case ADD -> add++;
            case REMOVE -> remove++;
            case REPLACE -> replace++;
        }
    }
}
