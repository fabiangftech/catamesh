package dev.catamesh.application.handler;


import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.application.adapter.SchemaAdapter;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.DataProductContext;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidateResourceSchemaHandler<Context> extends Handler<Context> {
    private final Schema resourceSchema;
    private final ObjectMapper jsonMapper;

    public ValidateResourceSchemaHandler(
            Schema resourceSchema,
            ObjectMapper jsonMapper) {
        this.resourceSchema = resourceSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(Context context) {
        DataProductContext dataProductContext= (DataProductContext)context;
        dataProductContext.getDesiredResources().forEach(resource -> {
            String resourceJson = SchemaPayloadAdapter.toJson(resource, jsonMapper);
            List<Error> schemaErrors = resourceSchema.validate(resourceJson, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
            if (!schemaErrors.isEmpty()) {
                String message = String.format("Error in resource with name=%s", dataProductContext.getDesiredDataProduct().getMetadata().getName());
                List<String> errors = SchemaAdapter.toList(schemaErrors);
                throw new SchemaException(message, errors);
            }
        });
    }
}
