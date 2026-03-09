package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffSummary;

public class BuildDiffSummaryHandler extends Handler<ApplyDataProductContext> {

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        DiffSummary summary = new DiffSummary();
        if (context.getDiffSections() != null) {
            context.getDiffSections()
                    .forEach(section -> section.getChanges().forEach(change -> summary.plus(change.getOp())));
        }
        context.setDiffSummary(summary);
    }
}
