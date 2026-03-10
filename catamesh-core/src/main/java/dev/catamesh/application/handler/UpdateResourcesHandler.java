package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;

public class UpdateResourcesHandler extends Handler<ApplyDataProductContext> {

    private final Command<Resource, Resource> updateResourceCommand;

    public UpdateResourcesHandler(Command<Resource, Resource> updateResourceCommand) {
        this.updateResourceCommand = updateResourceCommand;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (PlanResource planResource : context.getPlan().getResources()) {
            if (!planResource.getType().equals(PlanResourceType.RESOURCE)
                || !planResource.getAction().equals(PlanAction.UPDATE)) {
                continue;
            }

            Resource resource = context.getResources().stream()
                    .filter(item -> item.getName().equals(planResource.getName()))
                    .findFirst()
                    .orElseThrow(() -> new InvariantException(
                            String.format("Resource does not exist for name=%s", planResource.getName())
                    ));

            updateResourceCommand.execute(resource);
        }
    }
}
