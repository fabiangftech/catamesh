package dev.catamesh.core.model.diff;

import java.util.List;
import java.util.Map;

public interface NormalizedNode {

    NodeType type();

    default Map<String, NormalizedNode> fields() {
        throw new UnsupportedOperationException("Node is not an object");
    }

    default List<NormalizedNode> elements() {
        throw new UnsupportedOperationException("Node is not an array");
    }

    default Object value() {
        throw new UnsupportedOperationException("Node is not a value");
    }

    default boolean isObject() {
        return type() == NodeType.OBJECT;
    }

    default boolean isArray() {
        return type() == NodeType.ARRAY;
    }

    default boolean isValue() {
        return type() == NodeType.VALUE;
    }

    default boolean isNull() {
        return type() == NodeType.NULL;
    }
}
