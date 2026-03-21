package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum PlanStepType {
    METADATA("metadata"),
    SPEC("spec"),
    RESOURCE("resource"),
    RESOURCE_DEFINITION("resource-definition");

    private final String value;

    PlanStepType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PlanStepType fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Change type can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(String.format("Invalid change type %s", raw)));
    }
}
