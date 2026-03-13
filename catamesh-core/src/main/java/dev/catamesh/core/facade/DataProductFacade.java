package dev.catamesh.core.facade;

import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;

public interface DataProductFacade {
    DiffResult diff(String yaml);
    PlanResult plan(String yaml);
    ApplyResult apply(String yaml);
}
