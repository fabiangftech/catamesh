package dev.catamesh.application.handler;


import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;

public class CreateResourcesHandler extends Handler<ApplyDataProductContext> {
    private final Command<Resource, Void> createResourceCommand;

    public CreateResourcesHandler(Command<Resource, Void> createResourceCommand) {
        this.createResourceCommand = createResourceCommand;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (PlanResource planResource : context.getPlan().getResources()) {
            if (planResource.getType().equals(PlanResourceType.RESOURCE) && planResource.getAction().equals(PlanAction.CREATE)) {
                Resource resource = context.getResources().stream()
                        .filter(item -> item.getName().equals(planResource.getName()))
                        .findFirst()
                        .orElseThrow(() -> new InvariantException("Resource Don't exist."));
                resource.setDataProductId(context.getDataProductId());
                createResourceCommand.execute(resource);
            }
        }
    }
}
