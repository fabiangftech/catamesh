package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;

public class CreateResourceDefinitionsHandler extends Handler<ApplyDataProductContext> {
    private final Command<Resource, Resource> deactivateResourceDefinitionsByResourceIdCommand;
    private final Command<Resource, Resource> createResourceDefinitionCommand;

    public CreateResourceDefinitionsHandler(
            Command<Resource, Resource> deactivateResourceDefinitionsByResourceIdCommand,
            Command<Resource, Resource> createResourceDefinitionCommand) {
        this.deactivateResourceDefinitionsByResourceIdCommand = deactivateResourceDefinitionsByResourceIdCommand;
        this.createResourceDefinitionCommand = createResourceDefinitionCommand;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (PlanResource planResource : context.getPlan().getResources()) {
            if (!planResource.getType().equals(PlanResourceType.RESOURCE_DEFINITION)
                || !planResource.getAction().equals(PlanAction.CREATE)) {
                continue;
            }

            Resource resource = context.getResources().stream()
                    .filter(item -> item.getName().equals(planResource.getName()))
                    .findFirst()
                    .orElseThrow(() -> new InvariantException(
                            String.format("Resource does not exist for name=%s", planResource.getName())
                    ));

            deactivateResourceDefinitionsByResourceIdCommand.execute(resource);
            createResourceDefinitionCommand.execute(resource);
        }
    }
}
