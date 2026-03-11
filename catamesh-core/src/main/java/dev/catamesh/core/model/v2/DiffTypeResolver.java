package dev.catamesh.core.model.v2;

import java.util.Map;

public class DiffTypeResolver {

    private DiffTypeResolver() {
    }

    public static DiffNodeKind resolve(Object value) {
        if (value == null) {
            return DiffNodeKind.NULL;
        }

        Class<?> type = value.getClass();

        if (DiffTypeUtils.isValueType(type)) {
            return DiffNodeKind.VALUE;
        }

        if (value instanceof Map<?, ?>) {
            return DiffNodeKind.MAP;
        }

        if (value instanceof Iterable<?> || type.isArray()) {
            return DiffNodeKind.LIST;
        }

        return DiffNodeKind.OBJECT;
    }
}
