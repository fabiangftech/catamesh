package dev.catamesh.core.model.v2;


import dev.catamesh.core.builder.DiffEngineBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DiffEngine {
    private Set<String> ignoredPaths;

    public DiffEngine() {
    }

    public DiffEngine(DiffEngineBuilder builder) {
        this.ignoredPaths = builder.getIgnoredPaths();
    }

    public DiffResult compare(Object desired, Object current) {

        DiffTreeNode root = diffNode("", desired, current);

        return new DiffResult(root);
    }

    private DiffTreeNode diffNode(String path, Object desired, Object current) {

        DiffNodeKind kind = DiffTypeResolver.resolve(desired != null ? desired : current);

        return switch (kind) {

            case VALUE, NULL -> diffValue(path, desired, current);

            case OBJECT -> diffObject(path, desired, current);

            case MAP -> diffMap(path, desired, current);

            case LIST -> diffList(path, desired, current);
        };
    }

    private DiffTreeNode diffValue(String path, Object desired, Object current) {

        if (Objects.equals(desired, current)) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.NONE, current, desired, null, null, null);
        }

        if (current == null) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.CREATE, null, desired, null, null, null);
        }

        if (desired == null) {
            return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.DELETE, current, null, null, null, null);
        }

        return new DiffTreeNode(path, DiffNodeKind.VALUE, DiffChangeType.UPDATE, current, desired, null, null, null);
    }

    private DiffTreeNode diffObject(String path, Object desired, Object current) {

        Map<String, DiffTreeNode> fields = new LinkedHashMap<>();

        for (Field field : getFields(desired.getClass())) {

            Object dv = read(field, desired);
            Object cv = read(field, current);

            String child = childPath(path, field.getName());
            String childNormalized = normalizeListPath(child);
            if (ignoredPaths.contains(childNormalized)) {
                continue;
            }
            fields.put(field.getName(), diffNode(child, dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.OBJECT, DiffChangeType.NONE, current, desired, fields, null, null);
    }

    private DiffTreeNode diffMap(String path, Object desired, Object current) {

        Map<?, ?> dm = (Map<?, ?>) desired;
        Map<?, ?> cm = (Map<?, ?>) current;

        Map<String, DiffTreeNode> entries = new LinkedHashMap<>();

        Set<Object> keys = new LinkedHashSet<>();
        keys.addAll(dm.keySet());
        keys.addAll(cm.keySet());

        for (Object key : keys) {

            Object dv = dm.get(key);
            Object cv = cm.get(key);

            String child = childPath(path, key.toString());

            entries.put(key.toString(), diffNode(child, dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.MAP, DiffChangeType.NONE, current, desired, null, null, entries);
    }

    private DiffTreeNode diffList(String path, Object desired, Object current) {

        List<?> dl = toList(desired);
        List<?> cl = toList(current);

        List<DiffTreeNode> elements = new ArrayList<>();

        int max = Math.max(dl.size(), cl.size());

        for (int i = 0; i < max; i++) {

            Object dv = i < dl.size() ? dl.get(i) : null;
            Object cv = i < cl.size() ? cl.get(i) : null;

            elements.add(diffNode(path + "[" + i + "]", dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.LIST, DiffChangeType.NONE, current, desired, null, elements, null);
    }

    private List<Field> getFields(Class<?> type) {

        List<Field> fields = new ArrayList<>();

        while (type != null && type != Object.class) {

            for (Field f : type.getDeclaredFields()) {

                if (!Modifier.isStatic(f.getModifiers()) && !f.isSynthetic()) {
                    f.setAccessible(true);
                    fields.add(f);
                }
            }

            type = type.getSuperclass();
        }

        return fields;
    }

    private Object read(Field field, Object obj) {

        try {
            return obj == null ? null : field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<?> toList(Object value) {

        if (value instanceof List<?> list) return list;

        if (value instanceof Iterable<?> it) {

            List<Object> l = new ArrayList<>();

            for (Object o : it) {
                l.add(o);
            }

            return l;
        }

        return List.of();
    }

    private String childPath(String parent, String child) {

        if (parent == null || parent.isBlank()) {
            return child;
        }

        return parent + "." + child;
    }

    private String normalizeListPath(String path) {
        return path.replaceAll("\\[\\d+\\]", "[?]");
    }

    public static DiffEngineBuilder builder() {
        return new DiffEngineBuilder();
    }
}

