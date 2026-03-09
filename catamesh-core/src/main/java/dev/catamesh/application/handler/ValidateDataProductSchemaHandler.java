package dev.catamesh.application.handler;

import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.Error;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ValidateDataProductSchemaHandler extends Handler<ApplyDataProductContext> {
    private static final Logger logger = Logger.getLogger(ValidateDataProductSchemaHandler.class.getName());
    private final Schema dataProductSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDataProductSchemaHandler(
            Schema dataProductSchema,
            ObjectMapper jsonMapper) {
        this.dataProductSchema = dataProductSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        String json = jsonMapper.writeValueAsString(context.getDataProduct());
        List<Error> dataProductErrors = dataProductSchema.validate(json, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        if (!dataProductErrors.isEmpty()) {
            String message = String.format("Error in data product with name=%s", context.getDataProduct().getMetadata().getName());
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
}
