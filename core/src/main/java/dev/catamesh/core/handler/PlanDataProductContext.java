package dev.catamesh.core.handler;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.PlanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanDataProductContext extends DiffDataProductContext {

    private PlanResult planResult;

    protected PlanDataProductContext(String yaml) {
        super(yaml);
    }

    public PlanResult getPlanResult() {
        return Objects.isNull(planResult) ? new PlanResult() : planResult;
    }

    public void setPlanResult(PlanResult planResult) {
        this.planResult = planResult;
    }
}
