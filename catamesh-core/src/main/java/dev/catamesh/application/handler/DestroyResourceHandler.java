package dev.catamesh.application.handler;


import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.DestroyMode;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;

import java.util.Optional;

public class DestroyResourceHandler extends Handler<DestroyDataProductContext> {
    private final Command<Key, Void> deleteResourceCommand;

    public DestroyResourceHandler(Command<Key, Void> deleteResourceCommand) {
        this.deleteResourceCommand = deleteResourceCommand;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        if (!DestroyMode.APPLY.equals(context.getMode())) {
            return;
        }
        context.getPlan().getResources().stream()
                .filter(planResource -> planResource.getType().equals(PlanResourceType.RESOURCE))
                .filter(planResource -> planResource.getAction().equals(PlanAction.DELETE))
                .forEach(planResource -> findResource(context, planResource.getName())
                        .ifPresent(resource -> deleteResourceCommand.execute(resource.getKey())));
    }

    private Optional<Resource> findResource(DestroyDataProductContext context, String resourceName) {
        return context.getResources().stream()
                .filter(resource -> resource.getName().equals(resourceName))
                .findFirst();
    }
}
