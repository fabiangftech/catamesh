package dev.catamesh.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class DiffSupport {

    private DiffSupport() {
        // do nothing
    }

    public static List<Field> getFields(Class<?> type) {

        List<Field> fields = new ArrayList<>();

        while (type != null
               && type != Object.class
               && type != Enum.class
               && !isJdkType(type)) {

            for (Field field : type.getDeclaredFields()) {

                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                trySetAccessible(field);
                fields.add(field);
            }

            type = type.getSuperclass();
        }

        return fields;
    }

    private static boolean isJdkType(Class<?> type) {
        Package pkg = type.getPackage();
        if (pkg == null) {
            return false;
        }

        String name = pkg.getName();
        return name.startsWith("java.")
               || name.startsWith("javax.")
               || name.startsWith("jdk.")
               || name.startsWith("sun.");
    }
    private static void trySetAccessible(Field field) {
        try {
            field.setAccessible(true);
        } catch (InaccessibleObjectException e) {
            // Ignorar fields no accesibles del JDK o módulos cerrados
        }
    }

    public static Object read(Field field, Object obj) {
        try {
            return obj == null ? null : field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot read field: " + field.getName(), e);
        }
    }

    public static List<?> toList(Object value) {

        if (value instanceof List<?> list) {
            return list;
        }

        if (value instanceof Iterable<?> iterable) {
            List<Object> result = new ArrayList<>();
            for (Object item : iterable) {
                result.add(item);
            }
            return result;
        }

        return List.of();
    }

    public static String childPath(String parent, String child) {

        if (parent == null || parent.isBlank()) {
            return child;
        }

        return parent + "." + child;
    }

    public static String normalizeListPath(String path) {
        return path.replaceAll("\\[\\d+\\]", "[?]");
    }
}