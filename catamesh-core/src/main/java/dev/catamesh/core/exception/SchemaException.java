package dev.catamesh.core.exception;

import java.util.List;

public class SchemaException extends RuntimeException {
    private final List<String> errors;

    public SchemaException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
