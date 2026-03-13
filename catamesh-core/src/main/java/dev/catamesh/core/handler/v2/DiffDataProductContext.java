package dev.catamesh.core.handler.v2;

import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PolicyRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiffDataProductContext extends DataProductContext {
    private DiffResult diffResult;

    protected DiffDataProductContext(String yaml) {
        super(yaml);
    }

    public DiffResult getDiffResult() {
        return diffResult;
    }

    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }

    public static PlanDataProductContext create(String yaml){
        return new PlanDataProductContext(yaml);
    }
}
