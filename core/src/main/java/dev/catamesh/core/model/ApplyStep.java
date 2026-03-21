package dev.catamesh.core.model;

public class ApplyStep {
    private String path;
    private PlanStepType type;
    private PlanAction action;
    private ApplyStepStatus status;
    private String message;

    public ApplyStep() {
        // do nothing
    }

    public ApplyStep(String path,
                     PlanStepType type,
                     PlanAction action,
                     ApplyStepStatus status,
                     String message) {
        this.path = path;
        this.type = type;
        this.action = action;
        this.status = status;
        this.message = message;
    }

    public static ApplyStep from(PlanStep planStep) {
        return new ApplyStep(
                planStep.getPath(),
                planStep.getType(),
                planStep.getAction(),
                null,
                null
        );
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PlanStepType getType() {
        return type;
    }

    public void setType(PlanStepType type) {
        this.type = type;
    }

    public PlanAction getAction() {
        return action;
    }

    public void setAction(PlanAction action) {
        this.action = action;
    }

    public ApplyStepStatus getStatus() {
        return status;
    }

    public void setStatus(ApplyStepStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
