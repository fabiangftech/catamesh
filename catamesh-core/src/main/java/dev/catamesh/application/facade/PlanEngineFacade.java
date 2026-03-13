package dev.catamesh.application.facade;

import dev.catamesh.core.facade.PlanFacade;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.strategy.PlanStrategy;

import java.util.Objects;

public final class PlanEngineFacade implements PlanFacade {

    private final PlanStrategy planMetadataStrategy;
    private final PlanStrategy planSpecStrategy;

    public PlanEngineFacade(PlanStrategy planMetadataStrategy,
                            PlanStrategy planSpecStrategy) {
        this.planMetadataStrategy = planMetadataStrategy;
        this.planSpecStrategy = planSpecStrategy;
    }

    public PlanResult plan(DiffResult diff) {
        Objects.requireNonNull(diff, "diff is required");

        PlanResult result = new PlanResult();
        DiffTreeNode root = diff.getRoot();

        planMetadataStrategy.plan(result, root);
        planSpecStrategy.plan(result, root);

        return result;
    }
}