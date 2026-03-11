package dev.catamesh.core.model;

public record PolicyRule(String path,
                         PolicyLevel level,
                         String message) {
    public static PolicyRule create(String path, PolicyLevel level, String message) {
        return new PolicyRule(path, level, message);
    }
}
