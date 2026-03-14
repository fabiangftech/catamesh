package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyResult {
    private String dataProductName;
    private ApplyStatus status;
    private List<ApplyStep> steps;
    private ApplySummary summary;
    private Instant executedAt;

    public ApplyResult() {
        // do nothing
    }
}
