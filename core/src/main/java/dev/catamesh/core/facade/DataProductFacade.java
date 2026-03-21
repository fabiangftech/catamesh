package dev.catamesh.core.facade;

import dev.catamesh.core.model.*;

public interface DataProductFacade {
    ValidateResult validate(String yaml);
    DiffResult diff(String yaml);
    PlanResult plan(String yaml);
    ApplyResult apply(String yaml);
    DataProduct get(String dataProductNames);
}
