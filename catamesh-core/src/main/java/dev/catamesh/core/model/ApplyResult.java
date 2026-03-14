package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyResult {
    private String dataProductName;
    private ApplyStatus status;
    private List<ApplyStep> steps;
    private ApplySummary summary;
    private LocalDateTime executedAt;

    public ApplyResult() {
        // do nothing
    }

    public String getDataProductName() {
        return dataProductName;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public void setDataProductName(String dataProductName) {
        this.dataProductName = dataProductName;
    }
}
