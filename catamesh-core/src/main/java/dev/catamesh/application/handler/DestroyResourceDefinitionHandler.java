package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.DestroyMode;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import java.util.Optional;

public class DestroyResourceDefinitionHandler extends Handler<DestroyDataProductContext> {

    private final Command<GetResourceDefinitionDTO, Void> deleteResourceDefinitionCommand;

    public DestroyResourceDefinitionHandler(Command<GetResourceDefinitionDTO, Void> deleteResourceDefinitionCommand) {
        this.deleteResourceDefinitionCommand = deleteResourceDefinitionCommand;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        if (!DestroyMode.APPLY.equals(context.getMode())) {
            return;
        }
        context.getPlan().getResources().stream()
                .filter(planResource -> planResource.getType().equals(PlanResourceType.RESOURCE_DEFINITION))
                .filter(planResource -> planResource.getAction().equals(PlanAction.DELETE))
                .forEach(planResource -> findResource(context, planResource.getName()).ifPresent(resource ->
                        deleteResourceDefinitionCommand.execute(
                                GetResourceDefinitionDTO.create(resource.getId(), planResource.getVersion())
                        )
                ));
    }

    private Optional<Resource> findResource(DestroyDataProductContext context, String resourceName) {
        return context.getResources().stream()
                .filter(resource -> resource.getName().equals(resourceName))
                .findFirst();
    }
}
