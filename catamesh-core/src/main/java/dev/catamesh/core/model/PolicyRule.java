package dev.catamesh.core.model;

public record PolicyRule(String path,
                         PolicyLevel level,
                         String message) {
}
