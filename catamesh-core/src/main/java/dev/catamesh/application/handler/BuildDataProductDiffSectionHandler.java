package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffScope;
import dev.catamesh.core.model.DiffSection;

import java.util.ArrayList;
import java.util.List;

public class BuildDataProductDiffSectionHandler extends Handler<ApplyDataProductContext> {

    private final DiffComparisonSupport diffComparisonSupport;

    public BuildDataProductDiffSectionHandler(DiffComparisonSupport diffComparisonSupport) {
        this.diffComparisonSupport = diffComparisonSupport;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        context.setDiffSections(new ArrayList<>());

        List<DiffChange> dataProductChanges = diffComparisonSupport.compareDataProduct(
                context.getDataProduct(),
                context.getCurrentDataProduct()
        );

        if (!dataProductChanges.isEmpty()) {
            context.addDiffSection(
                    new DiffSection(
                            DiffScope.DATA_PRODUCT,
                            context.getDataProductName(),
                            dataProductChanges
                    )
            );
        }
    }
}
