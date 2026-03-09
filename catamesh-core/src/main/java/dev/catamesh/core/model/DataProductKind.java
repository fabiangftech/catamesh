package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum DataProductKind {

    SOURCE_ALIGNED("source-aligned");

    private final String value;

     DataProductKind(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DataProductKind fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Data Product type can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidInputException(String.format("Invalid data-product type %s",raw))
                );
    }
}
