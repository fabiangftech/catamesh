package dev.catamesh.application.adapter;

import com.networknt.schema.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class SchemaAdapter {

    private SchemaAdapter() {
        // do nothing
    }

    public static List<String> toList(List<Error> schemaErrors) {
        List<String> errors = new ArrayList<>();
        AtomicReference<String> message = new AtomicReference<>();
        schemaErrors.forEach(dataProductError -> {
            message.set(String.format("%s=%s", dataProductError.getProperty(), dataProductError.getMessage()));
            errors.add(message.get());
        });
        return errors;
    }
}
