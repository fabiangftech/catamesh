package dev.catamesh.infrastructure.dto;

public enum CLIErrorCodeDTO {
    VALIDATION_ERROR(20, "VALIDATION_ERROR", "Validation error"),
    CONFLICT_ERROR(21, "CONFLICT_ERROR", "Conflict"),
    NOT_FOUND_ERROR(22, "NOT_FOUND_ERROR", "Not found"),
    DEPENDENCY_ERROR(23, "DEPENDENCY_ERROR", "Dependency error"),
    INVARIANT_ERROR(24, "INVARIANT_ERROR", "Invariant error"),
    INTERNAL_ERROR(25, "INTERNAL_ERROR", "Internal error");

    private final int status;
    private final String value;
    private final String title;

    CLIErrorCodeDTO(int status, String value, String title) {
        this.status = status;
        this.value = value;
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}
