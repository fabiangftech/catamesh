package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.catamesh.core.exception.InvalidInputException;

import java.util.Arrays;

public enum SchemaVersion {
    DATA_PRODUCT_V1("data-product/v1"),
    BUCKET_V1("bucket/v1");

    private final String value;

    SchemaVersion(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SchemaVersion fromValue(String raw) {
        if (raw == null) {
            throw new InvalidInputException("Schema version can't be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(raw) || v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidInputException(String.format("Invalid schema version %s", raw))
                );
    }
}
