package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.PlanDataProductContext;
import dev.catamesh.core.model.PlanEngine;
import dev.catamesh.core.model.PlanResult;

public class BuildPlanDataProductHandler extends Handler<PlanDataProductContext> {
    private final PlanEngine planEngine;

    public BuildPlanDataProductHandler() {
        this(new PlanEngine());
    }

    public BuildPlanDataProductHandler(PlanEngine planEngine) {
        this.planEngine = planEngine;
    }

    @Override
    protected void doHandle(PlanDataProductContext context) {
        PlanResult planResult = planEngine.plan(
                context.getDesiredDataProduct(),
                context.getCurrentDataProduct(),
                context.getDiffResult()
        );
        planResult.setPolicyRules(context.getPolicyRules());
        context.setPlanResult(planResult);
    }
}
