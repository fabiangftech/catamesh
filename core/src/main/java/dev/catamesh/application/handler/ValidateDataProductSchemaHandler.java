package dev.catamesh.application.handler;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import dev.catamesh.infrastructure.adapter.SchemaAdapter;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;
import dev.catamesh.core.model.PolicyLevel;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.infrastructure.adapter.SchemaPayloadAdapter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidateDataProductSchemaHandler<C> extends Handler<C> {
    private final Schema dataProductSchema;
    private final ObjectMapper jsonMapper;

    public ValidateDataProductSchemaHandler(
            Schema dataProductSchema,
            ObjectMapper jsonMapper) {
        this.dataProductSchema = dataProductSchema;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doHandle(C context) {
        ValidateDataProductContext validateDataProductContext = (ValidateDataProductContext) context;
        String json = SchemaPayloadAdapter.toJson(validateDataProductContext.getDesiredDataProduct(), jsonMapper);
        List<Error> schemaErrors = dataProductSchema.validate(json, InputFormat.JSON, executionContext -> executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        if (!schemaErrors.isEmpty()) {
            String message = String.format("Error in data product with name=%s", validateDataProductContext.getDesiredDataProduct().getMetadata().getName());
            List<String> errors = SchemaAdapter.toList(schemaErrors);
            validateDataProductContext.addPolicyRule(PolicyRule.create("", PolicyLevel.ERROR, message));
        }
    }
}
