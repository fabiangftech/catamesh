package dev.catamesh.application.handler;


import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateResourceSchemaHandler extends Handler<ApplyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateResourceSchemaHandler.class.getName());
    private final Schema resourceSchema;
    private final ObjectMapper jsonMapper;

    public ValidateResourceSchemaHandler(
            Schema resourceSchema,
            ObjectMapper jsonMapper) {
        this.resourceSchema = resourceSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        logger.info("Validate resource schema handler");
        context.getResources().forEach(resource -> {
            String resourceJson = jsonMapper.writeValueAsString(resource);
            logger.info(String.format("resourceJson=%s", resourceJson));
            List<Error> resourceErrors = resourceSchema.validate(resourceJson, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
            if (!resourceErrors.isEmpty()) {
                String message = String.format("Error in resource with name=%s", context.getDataProduct().getMetadata().getName());
                List<String> errors = new ArrayList<>();
                AtomicReference<String> messageError = new AtomicReference<>();
                resourceErrors.forEach(resourceError -> {
                    messageError.set(String.format("%s=%s", resourceError.getProperty(), resourceError.getMessage()));
                    logger.severe(messageError.get());
                    errors.add(messageError.get());
                });
                throw new SchemaException(message, errors);
            }
        });
    }
}
