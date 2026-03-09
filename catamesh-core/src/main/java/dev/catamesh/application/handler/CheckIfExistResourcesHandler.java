package dev.catamesh.application.handler;


import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.dto.GetResourceDTO;

import java.util.Optional;

public class CheckIfExistResourcesHandler extends Handler<ApplyDataProductContext> {

    private final Query<GetResourceDTO, Optional<Resource>> optionalResourceByNameAndDataProductIdQuery;

    public CheckIfExistResourcesHandler(Query<GetResourceDTO, Optional<Resource>> optionalResourceByNameAndDataProductIdQuery) {
        this.optionalResourceByNameAndDataProductIdQuery = optionalResourceByNameAndDataProductIdQuery;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (Resource resource : context.getResources()) {
            GetResourceDTO dto = GetResourceDTO.create(
                    resource.getName(),
                    context.getDataProduct().getMetadata().getId()
            );

            PlanAction action = optionalResourceByNameAndDataProductIdQuery.execute(dto)
                    .map(current -> {
                        resource.setId(Key.create(current.getId()));
                        return Resource.isSame(current, resource) ? PlanAction.NOOP : PlanAction.UPDATE;
                    })
                    .orElse(PlanAction.CREATE);

            switch (action) {
                case CREATE -> context.plusCreateSummary();
                case UPDATE -> context.plusUpdateSummary();
                default -> context.plusNoopSummary();
            }

            context.getPlan().addResource(
                    PlanResource.resource(resource.getName(), action)
            );
        }
    }
}
