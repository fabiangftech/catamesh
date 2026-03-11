package dev.catamesh.core.model.diff;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ObjectNode implements NormalizedNode {

    private final Map<String, NormalizedNode> fields;

    public ObjectNode(Map<String, NormalizedNode> fields) {
        this.fields = Map.copyOf(fields);
    }

    public static ObjectNode empty() {
        return new ObjectNode(new LinkedHashMap<>());
    }

    @Override
    public NodeType type() {
        return NodeType.OBJECT;
    }

    @Override
    public Map<String, NormalizedNode> fields() {
        return fields;
    }
}
