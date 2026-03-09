package dev.catamesh.application.handler;


import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateDestroyResourceSchemaHandler extends Handler<DestroyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateDestroyResourceSchemaHandler.class.getName());
    private final Schema resourceSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDestroyResourceSchemaHandler(
            Schema resourceSchema,
            ObjectMapper jsonMapper) {
        this.resourceSchema = resourceSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        context.getRequestedResources().forEach(resource -> {
            String resourceJson = jsonMapper.writeValueAsString(resource);
            List<Error> resourceErrors = resourceSchema.validate(
                    resourceJson,
                    InputFormat.JSON,
                    executionContext -> executionContext.executionConfig(config -> config.formatAssertionsEnabled(true))
            );
            if (resourceErrors.isEmpty()) {
                return;
            }

            String message = String.format(
                    "Error in destroy request resource for data product with name=%s",
                    context.getName()
            );
            List<String> errors = new ArrayList<>();
            AtomicReference<String> messageError = new AtomicReference<>();
            resourceErrors.forEach(resourceError -> {
                messageError.set(String.format("%s=%s", resourceError.getProperty(), resourceError.getMessage()));
                logger.severe(messageError.get());
                errors.add(messageError.get());
            });
            throw new SchemaException(message, errors);
        });
    }
}
