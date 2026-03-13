package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.model.PlanEngine;
import dev.catamesh.core.model.PlanResult;

public class BuildPlanDataProductHandler<Context> extends Handler<Context> {
    private final PlanEngine planEngine;

    public BuildPlanDataProductHandler() {
        this(new PlanEngine());
    }

    public BuildPlanDataProductHandler(PlanEngine planEngine) {
        this.planEngine = planEngine;
    }

    @Override
    protected void doHandle(Context context) {
        PlanDataProductContext planDataProductContext=(PlanDataProductContext)context;
        PlanResult planResult = planEngine.plan(
                planDataProductContext.getDesiredDataProduct(),
                planDataProductContext.getCurrentDataProduct(),
                planDataProductContext.getDiffResult()
        );
        planResult.setPolicyRules(planDataProductContext.getPolicyRules());
        planDataProductContext.setPlanResult(planResult);
    }
}
