package dev.catamesh.core.handler;

import dev.catamesh.core.model.ApplyResult;

public class ApplyDataProductContext extends PlanDataProductContext {

    private ApplyResult applyResult;
    protected ApplyDataProductContext(String yaml) {
        super(yaml);
    }

    public ApplyResult getApplyResult() {
        return applyResult;
    }

    public void setApplyResult(ApplyResult applyResult) {
        this.applyResult = applyResult;
    }

    public static ApplyDataProductContext create(String yaml){
        return new ApplyDataProductContext(yaml);
    }
}
