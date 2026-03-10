package dev.catamesh.application.handler;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateDestroyBucketDefinitionSchemaHandler extends Handler<DestroyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateDestroyBucketDefinitionSchemaHandler.class.getName());
    private final Schema bucketSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDestroyBucketDefinitionSchemaHandler(Schema bucketSchema,
                                                        ObjectMapper jsonMapper) {
        this.bucketSchema = bucketSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        context.getRequestedResources().forEach(resource -> {
            if (!resource.getKind().equals(ResourceKind.BUCKET)) {
                return;
            }

            String resourceDefinitionJson = SchemaPayloadAdapter.toJson(resource.getDefinition(), jsonMapper);
            List<Error> bucketErrors = bucketSchema.validate(
                    resourceDefinitionJson,
                    InputFormat.JSON,
                    executionContext -> executionContext.executionConfig(config -> config.formatAssertionsEnabled(true))
            );
            if (bucketErrors.isEmpty()) {
                return;
            }

            String message = String.format("Error in bucket definition for destroy request resource=%s", resource.getName());
            logger.severe(message);
            List<String> errors = new ArrayList<>();
            AtomicReference<String> messageError = new AtomicReference<>();
            bucketErrors.forEach(bucketError -> {
                messageError.set(String.format("%s=%s", bucketError.getProperty(), bucketError.getMessage()));
                logger.severe(messageError.get());
                errors.add(messageError.get());
            });
            throw new SchemaException(message, errors);
        });
    }
}
