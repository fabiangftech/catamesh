package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.ApplyStep;
import dev.catamesh.core.model.ApplyStepStatus;
import dev.catamesh.core.model.ApplySummary;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanStepType;

import java.util.ArrayList;
import java.util.List;

public final class ApplyToStringAdapter {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    private ApplyToStringAdapter() {
        // do nothing
    }

    public static String toString(ApplyResult applyResult) {
        if (applyResult == null) {
            return "Apply: null";
        }

        List<String> lines = new ArrayList<>();
        List<ApplyStep> steps = applyResult.getSteps() == null ? List.of() : applyResult.getSteps();
        ApplySummary summary = applyResult.getSummary() == null ? new ApplySummary(0, 0, 0) : applyResult.getSummary();

        for (ApplyStep step : steps) {
            if (step == null || step.getAction() == null) {
                continue;
            }
            lines.add(formatStep(step));
        }

        if (lines.isEmpty()) {
            lines.add("No changes");
        }

        lines.add("");
        lines.add(CYAN + "Summary: "
                  + summary.getExecuted() + " executed, "
                  + summary.getSkipped() + " skipped, "
                  + summary.getFailed() + " failed"
                  + RESET);

        return String.join(System.lineSeparator(), lines);
    }

    private static String formatStep(ApplyStep step) {
        String type = normalizeType(step.getType());
        String path = normalizePath(step.getPath());
        String status = formatStatus(step.getStatus());
        String message = formatMessage(step.getMessage());

        return switch (step.getAction()) {
            case CREATE -> GREEN + "+ [" + type + "] " + path + RESET + status + message;
            case UPDATE, REPLACE -> YELLOW + "~ [" + type + "] " + path + RESET + status + message;
            case DELETE -> RED + "- [" + type + "] " + path + RESET + status + message;
            case NOOP -> "= [" + type + "] " + path + status + message;
        };
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "<root>";
        }
        return path;
    }

    private static String normalizeType(PlanStepType type) {
        if (type == null) {
            return "unknown";
        }
        return type.getValue();
    }

    private static String formatStatus(ApplyStepStatus status) {
        if (status == null) {
            return "";
        }
        return " -> " + status.name();
    }

    private static String formatMessage(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        return " - " + message;
    }
}
