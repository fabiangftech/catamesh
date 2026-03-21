package dev.catamesh.core.handler;

import dev.catamesh.core.model.DiffResult;

public class DiffDataProductContext extends ValidateDataProductContext {
    private DiffResult diffResult;

    protected DiffDataProductContext(String yaml) {
        super(yaml);
    }

    public DiffResult getDiffResult() {
        return diffResult;
    }

    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }

    public static PlanDataProductContext create(String yaml){
        return new PlanDataProductContext(yaml);
    }
}
