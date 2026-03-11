package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

@Deprecated
public enum DiffOp {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace");

    private final String value;

    DiffOp(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DiffOp fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Diff operation can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidInputException(String.format("Invalid diff operation %s", raw))
                );
    }
}
