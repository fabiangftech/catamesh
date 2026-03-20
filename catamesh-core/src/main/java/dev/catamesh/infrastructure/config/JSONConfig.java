package dev.catamesh.infrastructure.config;

import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SchemaRegistryConfig;
import com.networknt.schema.SpecificationVersion;
import com.networknt.schema.regex.JDKRegularExpressionFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Locale;

public final class JSONConfig {
    private static final String SCHEMA_DATA_PRODUCT_V1 = "schemas/data-product.v1.schema.json";
    private static final String SCHEMA_RESOURCE_V1 = "schemas/resource.v1.schema.json";
    private static final String SCHEMA_DEFINITION_BUCKET_V1 = "schemas/definition-bucket.v1.schema.json";

    private JSONConfig() {
        // do nothing
    }

    public static ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    public static Schema dataProductSchema() {
        InputStream in = JSONConfig.class.getClassLoader().getResourceAsStream(SCHEMA_DATA_PRODUCT_V1);
        return schemaRegistry().getSchema(in);
    }

    public static Schema resourceSchema() {
        InputStream in = JSONConfig.class.getClassLoader().getResourceAsStream(SCHEMA_RESOURCE_V1);
        return schemaRegistry().getSchema(in);
    }

    public static Schema bucketSchema() {
        InputStream in = JSONConfig.class.getClassLoader().getResourceAsStream(SCHEMA_DEFINITION_BUCKET_V1);
        return schemaRegistry().getSchema(in);
    }

    private static SchemaRegistry schemaRegistry() {
        return SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder.schemaRegistryConfig(schemaRegistryConfig()));
    }

    private static SchemaRegistryConfig schemaRegistryConfig() {
        return SchemaRegistryConfig.builder()
                .locale(Locale.ENGLISH)
                .regularExpressionFactory(JDKRegularExpressionFactory.getInstance()).build();
    }
}
