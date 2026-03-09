package dev.catamesh.application.handler;


import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidateDestroyDefinitionVersionHandler extends Handler<DestroyDataProductContext> {

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        List<String> errors = new ArrayList<>();
        for (Resource resource : context.getRequestedResources()) {
            if (Objects.isNull(resource.getDefinition())
                || Objects.isNull(resource.getDefinition().getVersion())
                || resource.getDefinition().getVersion().isBlank()) {
                errors.add(String.format("resource=%s requires definition.version for destroy", resource.getName()));
            }
        }
        if (!errors.isEmpty()) {
            throw new SchemaException("Error in destroy request resources", errors);
        }
    }
}
