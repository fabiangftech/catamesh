package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum PolicyLevel {
    ERROR("error");

    private final String value;

    PolicyLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PolicyLevel fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Change policy level can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(String.format("Invalid policy level %s", raw)));
    }
}

