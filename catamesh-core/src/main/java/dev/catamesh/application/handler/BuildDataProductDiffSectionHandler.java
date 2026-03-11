package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffScope;
import dev.catamesh.core.model.DiffSection;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BuildDataProductDiffSectionHandler extends Handler<ApplyDataProductContext> {

    private final DiffStrategy<DataProduct> diffStrategy;

    public BuildDataProductDiffSectionHandler(DiffStrategy<DataProduct> diffStrategy) {
        this.diffStrategy = diffStrategy;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        context.setDiffSections(new ArrayList<>());

        List<DiffChange> dataProductChanges = diffStrategy.compare(
                context.getDataProduct(),
                context.getCurrentDataProduct(),
                ""
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
