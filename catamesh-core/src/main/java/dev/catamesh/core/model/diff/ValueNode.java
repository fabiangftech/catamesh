package dev.catamesh.core.model.diff;

public final class ValueNode implements NormalizedNode {

    private final Object value;

    public ValueNode(Object value) {
        this.value = value;
    }

    @Override
    public NodeType type() {
        return NodeType.VALUE;
    }

    @Override
    public Object value() {
        return value;
    }
}
