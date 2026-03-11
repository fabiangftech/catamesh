package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffScope;
import dev.catamesh.core.model.DiffSection;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.strategy.DiffOLDStrategy;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BuildDataProductDiffSectionHandler extends Handler<ApplyDataProductContext> {

    private final DiffOLDStrategy<DataProduct> diffOLDStrategy;

    public BuildDataProductDiffSectionHandler(DiffOLDStrategy<DataProduct> diffOLDStrategy) {
        this.diffOLDStrategy = diffOLDStrategy;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        context.setDiffSections(new ArrayList<>());

        List<DiffChange> dataProductChanges = diffOLDStrategy.compare(
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
