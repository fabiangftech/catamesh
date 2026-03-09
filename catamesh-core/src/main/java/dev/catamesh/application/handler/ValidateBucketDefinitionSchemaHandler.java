package dev.catamesh.application.handler;

import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.Error;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.ResourceKind;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateBucketDefinitionSchemaHandler extends Handler<ApplyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateBucketDefinitionSchemaHandler.class.getName());
    private final Schema bucketSchema;
    private final ObjectMapper jsonMapper;

    public ValidateBucketDefinitionSchemaHandler(Schema bucketSchema,
                                                 ObjectMapper jsonMapper) {

        this.bucketSchema = bucketSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        logger.info("Validate bucket schema handler");
        context.getResources().forEach(resource -> {
            if (resource.getKind().equals(ResourceKind.BUCKET)) {
                String resourceDefinitionJson = jsonMapper.writeValueAsString(resource.getDefinition());
                logger.info(String.format("resourceDefinitionJson=%s", resourceDefinitionJson));
                List<Error> bucketErrors = bucketSchema.validate(resourceDefinitionJson, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
                if (!bucketErrors.isEmpty()) {
                    String message = String.format("Error in bucket definition for resource=%s", resource.getName());
                    logger.severe(message);
                    List<String> errors = new ArrayList<>();
                    AtomicReference<String> messageError = new AtomicReference<>();

                    bucketErrors.forEach(bucketError -> {
                        messageError.set(String.format("%s=%s", bucketError.getProperty(), bucketError.getMessage()));
                        logger.severe(messageError.get());
                        errors.add(messageError.get());
                    });
                    throw new SchemaException(message, errors);
                }
            }
        });
    }
}
