package dev.catamesh.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanResult {
    private PlanSummary summary;
    private List<PlanStep> steps;
    private List<PolicyRule> policyRules;

    public PlanResult() {
        this(new PlanSummary(), new ArrayList<>(), new ArrayList<>());
    }

    public PlanResult(PlanSummary summary, List<PlanStep> steps) {
        this(summary, steps, new ArrayList<>());
    }

    public PlanResult(PlanSummary summary, List<PlanStep> steps, List<PolicyRule> policyRules) {
        this.summary = Objects.requireNonNullElseGet(summary, PlanSummary::new);
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
        this.policyRules = policyRules == null ? new ArrayList<>() : new ArrayList<>(policyRules);
    }

    public PlanSummary getSummary() {
        return summary;
    }

    public void setSummary(PlanSummary summary) {
        this.summary = Objects.requireNonNullElseGet(summary, PlanSummary::new);
    }

    public List<PlanStep> getSteps() {
        return steps;
    }

    public void setSteps(List<PlanStep> steps) {
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
    }

    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }

    public void setPolicyRules(List<PolicyRule> policyRules) {
        this.policyRules = policyRules == null ? new ArrayList<>() : new ArrayList<>(policyRules);
    }
}
