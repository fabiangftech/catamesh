package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStep;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.model.PlanSummary;
import dev.catamesh.core.model.PolicyRule;

import java.util.ArrayList;
import java.util.List;

public final class PlantResultAdapter {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    private PlantResultAdapter() {
        // do nothing
    }

    public static String toString(PlanResult planResult) {
        if (planResult == null) {
            return "Plan: null";
        }

        List<String> lines = new ArrayList<>();
        List<PlanStep> steps = planResult.getSteps() == null ? List.of() : planResult.getSteps();
        List<PolicyRule> policyRules = planResult.getPolicyRules() == null ? List.of() : planResult.getPolicyRules();
        PlanSummary summary = planResult.getSummary() == null ? new PlanSummary() : planResult.getSummary();

        for (PlanStep step : steps) {
            if (step == null || step.getAction() == null || step.getAction() == PlanAction.NOOP) {
                continue;
            }
            lines.add(formatStep(step));
        }

        if (lines.isEmpty()) {
            lines.add("No changes");
        }

        if (!policyRules.isEmpty()) {
            lines.add("");
            lines.add("Policy rules:");
            for (PolicyRule policyRule : policyRules) {
                if (policyRule != null) {
                    lines.add(ConsoleFormatterAdapter.formatPolicyRule(policyRule));
                }
            }
        }

        lines.add("");
        lines.add(CYAN + "Summary: "
                  + summary.getCreate() + " create, "
                  + summary.getUpdate() + " update, "
                  + summary.getDelete() + " delete, "
                  + summary.getNoop() + " noop"
                  + RESET);

        return String.join(System.lineSeparator(), lines);
    }

    private static String formatStep(PlanStep step) {
        String type = normalizeType(step.getType());
        String path = normalizePath(step.getPath());

        return switch (step.getAction()) {
            case CREATE -> GREEN + "+ [" + type + "] " + path + RESET
                           + " = " + stringify(step.getAfter());
            case UPDATE, REPLACE -> YELLOW + "~ [" + type + "] " + path + RESET
                                    + " = " + stringify(step.getBefore())
                                    + " -> " + stringify(step.getAfter());
            case DELETE -> RED + "- [" + type + "] " + path + RESET
                           + " = " + stringify(step.getBefore());
            case NOOP -> "";
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

    private static String stringify(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        return String.valueOf(value);
    }
}
