package dev.catamesh.core.model.diff;

public final class NullNode implements NormalizedNode {

    public static final NullNode INSTANCE = new NullNode();

    private NullNode() {
    }

    @Override
    public NodeType type() {
        return NodeType.NULL;
    }
}
