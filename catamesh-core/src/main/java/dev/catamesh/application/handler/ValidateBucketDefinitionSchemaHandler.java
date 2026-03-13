package dev.catamesh.application.handler;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.application.adapter.SchemaAdapter;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.DataProductContext;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidateBucketDefinitionSchemaHandler<Context> extends Handler<Context> {
    private final Schema bucketSchema;
    private final ObjectMapper jsonMapper;

    public ValidateBucketDefinitionSchemaHandler(Schema bucketSchema,
                                                 ObjectMapper jsonMapper) {

        this.bucketSchema = bucketSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(Context context) {
        DataProductContext dataProductContext = (DataProductContext) context;
        dataProductContext.getDesiredResources().forEach(resource -> {
            if (resource.getKind().equals(ResourceKind.BUCKET)) {
                String json = SchemaPayloadAdapter.toJson(resource.getDefinition(), jsonMapper);
                List<Error> schemaErrors = bucketSchema.validate(json, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
                if (!schemaErrors.isEmpty()) {
                    String message = String.format("Error in bucket definition for resource=%s", resource.getName());
                    List<String> errors = SchemaAdapter.toList(schemaErrors);
                    throw new SchemaException(message, errors);
                }
            }
        });
    }
}
