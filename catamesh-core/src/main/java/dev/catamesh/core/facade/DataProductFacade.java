package dev.catamesh.core.facade;


import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Diff;
import dev.catamesh.core.model.Plan;

public interface DataProductFacade {

    Plan plan(String yaml);
    Diff diff(String yaml);
    Plan planDestroy(String yaml);
    void apply(String yaml);
    DataProduct get(String name);
    void applyDestroy(String yaml);
}
