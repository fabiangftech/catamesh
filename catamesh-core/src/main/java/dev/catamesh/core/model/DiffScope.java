package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum DiffScope {
    DATA_PRODUCT("dataProduct"),
    RESOURCE("resource");

    private final String value;

    DiffScope(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DiffScope fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Diff scope can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidInputException(String.format("Invalid diff scope %s", raw))
                );
    }
}
