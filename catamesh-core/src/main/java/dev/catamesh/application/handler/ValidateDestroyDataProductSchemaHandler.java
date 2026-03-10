package dev.catamesh.application.handler;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateDestroyDataProductSchemaHandler extends Handler<DestroyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateDestroyDataProductSchemaHandler.class.getName());
    private final Schema dataProductSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDestroyDataProductSchemaHandler(
            Schema dataProductSchema,
            ObjectMapper jsonMapper) {
        this.dataProductSchema = dataProductSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        String json = SchemaPayloadAdapter.toJson(context.getRequestDataProduct(), jsonMapper);
        List<Error> dataProductErrors = dataProductSchema.validate(
                json,
                InputFormat.JSON,
                executionContext -> executionContext.executionConfig(config -> config.formatAssertionsEnabled(true))
        );
        if (dataProductErrors.isEmpty()) {
            return;
        }

        String message = String.format(
                "Error in destroy request for data product with name=%s",
                context.getRequestDataProduct().getMetadata().getName()
        );
        List<String> errors = new ArrayList<>();
        AtomicReference<String> messageError = new AtomicReference<>();
        dataProductErrors.forEach(dataProductError -> {
            messageError.set(String.format("%s=%s", dataProductError.getProperty(), dataProductError.getMessage()));
            logger.severe(messageError.get());
            errors.add(messageError.get());
        });
        throw new SchemaException(message, errors);
    }
}
