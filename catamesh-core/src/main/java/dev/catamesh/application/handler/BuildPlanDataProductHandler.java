package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.application.facade.PlanEngineFacade;
import dev.catamesh.core.model.PlanResult;

public class BuildPlanDataProductHandler<Context> extends Handler<Context> {
    private final PlanEngineFacade planEngineFacade;
    public BuildPlanDataProductHandler(PlanEngineFacade planEngineFacade) {
        this.planEngineFacade = planEngineFacade;
    }

    @Override
    protected void doHandle(Context context) {
        PlanDataProductContext planDataProductContext=(PlanDataProductContext)context;
        PlanResult planResult = planEngineFacade.plan(planDataProductContext.getDiffResult());
        planResult.setPolicyRules(planDataProductContext.getPolicyRules());
        planDataProductContext.setPlanResult(planResult);
    }
}
