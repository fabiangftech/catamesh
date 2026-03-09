package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum ModelType {
    DATA_PRODUCT("data-product"),
    DEPLOY("deploy"),
    ENVIRONMENT("env");

    private final String value;

    ModelType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ModelType fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Model type type can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidInputException(String.format("Invalid model type %s", raw))
                );
    }
}
