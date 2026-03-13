package dev.catamesh.core.facade;

import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;

public interface PlanFacade {
    PlanResult plan(DiffResult diff);
}
