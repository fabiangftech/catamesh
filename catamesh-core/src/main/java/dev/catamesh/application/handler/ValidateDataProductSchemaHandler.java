package dev.catamesh.application.handler;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.application.adapter.SchemaAdapter;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DataProductContext;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidateDataProductSchemaHandler<Context> extends Handler<Context> {
    private final Schema dataProductSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDataProductSchemaHandler(
            Schema dataProductSchema,
            ObjectMapper jsonMapper) {
        this.dataProductSchema = dataProductSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(Context context) {
        DataProductContext dataProductContext = (DataProductContext) context;
        String json = SchemaPayloadAdapter.toJson(dataProductContext.getDesiredDataProduct(), jsonMapper);
        List<Error> schemaErrors = dataProductSchema.validate(json, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        if (!schemaErrors.isEmpty()) {
            String message = String.format("Error in data product with name=%s", dataProductContext.getDesiredDataProduct().getMetadata().getName());
            List<String> errors = SchemaAdapter.toList(schemaErrors);
            throw new SchemaException(message, errors);
        }
    }
}
