package dev.catamesh.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLIErrorPayloadDTO {

    private final String errorCode;
    private final int status;
    private final String title;
    private final String message;
    private final String hint;
    private final List<String> details;

    public CLIErrorPayloadDTO(
            String errorCode,
            int status,
            String title,
            String message,
            String hint,
            List<String> details) {
        this.errorCode = errorCode;
        this.status = status;
        this.title = title;
        this.message = message;
        this.hint = hint;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getHint() {
        return hint;
    }

    public List<String> getDetails() {
        return details;
    }
}
