package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public ApplyStatus getStatus() {
        return status;
    }

    public void setStatus(ApplyStatus status) {
        this.status = status;
    }
    public List<ApplyStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ApplyStep> steps) {
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
    }

    public ApplySummary getSummary() {
        return summary;
    }

    public void setSummary(ApplySummary summary) {
        this.summary = summary;
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
