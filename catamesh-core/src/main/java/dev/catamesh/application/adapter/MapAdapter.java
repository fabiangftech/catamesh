package dev.catamesh.application.adapter;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MapAdapter {
    public static Map<String, Object> asStringMap(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }

        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            copy.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return copy;
    }

    public static  Map<String, Object> nestedMap(Object value, String key){
        Map<String, Object> map = asStringMap(value);
        if (map == null) {
            return Map.of();
        }

        Map<String, Object> nested = asStringMap(map.get(key));
        return nested == null ? Map.of() : nested;
    }

    public static Map<String, Object> filterMap(Object value, String excludedKey){
        Map<String, Object> map = asStringMap(value);
        if (map == null) {
            return null;
        }

        Map<String, Object> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!excludedKey.equals(entry.getKey())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }
}
