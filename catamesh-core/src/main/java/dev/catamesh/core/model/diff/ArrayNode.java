package dev.catamesh.core.model.diff;

import java.util.List;

public final class ArrayNode implements NormalizedNode {

    private final List<NormalizedNode> elements;

    public ArrayNode(List<NormalizedNode> elements) {
        this.elements = List.copyOf(elements);
    }

    @Override
    public NodeType type() {
        return NodeType.ARRAY;
    }

    @Override
    public List<NormalizedNode> elements() {
        return elements;
    }
}
