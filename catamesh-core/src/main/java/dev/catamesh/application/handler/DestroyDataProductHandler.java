package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.DestroyMode;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.Resource;

import java.util.List;

public class DestroyDataProductHandler extends Handler<DestroyDataProductContext> {
    private final Command<String, Void> deleteDataProductCommand;
    private final Query<String, List<Resource>> allResourcesQuery;

    public DestroyDataProductHandler(Command<String, Void> deleteDataProductCommand,
                                     Query<String, List<Resource>> allResourcesQuery) {
        this.deleteDataProductCommand = deleteDataProductCommand;
        this.allResourcesQuery = allResourcesQuery;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        if (!DestroyMode.APPLY.equals(context.getMode())) {
            return;
        }
        if (!PlanAction.DELETE.equals(context.getPlan().getAction())) {
            return;
        }
        if (allResourcesQuery.execute(context.getName()).isEmpty()) {
            deleteDataProductCommand.execute(context.getName());
        }
    }
}
