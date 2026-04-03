package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.ApplyStep;
import dev.catamesh.core.model.PlanStep;

public final class ApplyStepAdapter {

    private ApplyStepAdapter(){
        // do nothing
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
}
