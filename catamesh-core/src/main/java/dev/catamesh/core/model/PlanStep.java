package dev.catamesh.core.model;

public class PlanStep {
    private PlanStepType type;
    private String path;
    private PlanAction action;
    private Object before;
    private Object after;

    public PlanStep() {
    }

    public PlanStep(PlanStepType type, String path, PlanAction action, Object before, Object after) {
        this.type = type;
        this.path = path;
        this.action = action;
        this.before = before;
        this.after = after;
    }

    public PlanStepType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public PlanAction getAction() {
        return action;
    }

    public Object getBefore() {
        return before;
    }

    public Object getAfter() {
        return after;
    }
}
