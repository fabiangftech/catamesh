package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Diff;
import dev.catamesh.core.model.DiffSection;
import dev.catamesh.core.model.DiffSummary;

import java.util.Collections;
import java.util.List;

public class BuildDiffResultHandler extends Handler<ApplyDataProductContext> {

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        List<DiffSection> sections = context.getDiffSections() == null
                ? Collections.emptyList()
                : context.getDiffSections();
        DiffSummary summary = context.getDiffSummary() == null
                ? new DiffSummary()
                : context.getDiffSummary();
        context.setDiffResult(new Diff(context.getDataProductName(), summary, sections));
    }
}
