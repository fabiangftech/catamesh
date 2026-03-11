package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.infrastructure.config.JSONConfig;
import dev.catamesh.infrastructure.dto.GetResourceDTO;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import dev.catamesh.support.H2TestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

class DbQueryCoverageTest {

    private final JSONConfig jsonConfig = new JSONConfig();

    @Test
    void queriesReturnExpectedRowsAndOrdering() throws Exception {
        DataSource dataSource = initializedDataSource();
        seedSalesGraph(dataSource);

        OptionalDataProductQuery optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        Optional<DataProduct> dataProduct = optionalDataProductQuery.execute("sales");
        Assertions.assertTrue(dataProduct.isPresent());
        Assertions.assertEquals("sales", dataProduct.get().getMetadata().getName());

        AllResourcesQuery allResourcesQuery = new AllResourcesQuery(dataSource);
        List<Resource> resources = allResourcesQuery.execute("sales");
        Assertions.assertEquals(List.of("customers", "orders"), resources.stream().map(Resource::getName).toList());

        OptionalResourceQuery optionalResourceQuery = new OptionalResourceQuery(dataSource);
        Optional<Resource> resource = optionalResourceQuery.execute(GetResourceDTO.create("orders", "dp-sales"));
        Assertions.assertTrue(resource.isPresent());
        Assertions.assertEquals("resource-orders", resource.get().getId());

        CountResourceDefinitionsByResourceIdQuery countQuery = new CountResourceDefinitionsByResourceIdQuery(dataSource);
        Assertions.assertEquals(2, countQuery.execute(Key.create("resource-orders")));
        Assertions.assertEquals(0, countQuery.execute(Key.create("missing")));

        OptionalResourceDefinitionQuery optionalResourceDefinitionQuery = new OptionalResourceDefinitionQuery(dataSource);
        Assertions.assertTrue(optionalResourceDefinitionQuery.execute(
                GetResourceDefinitionDTO.create("resource-orders", "0.0.1")
        ).isPresent());
        Assertions.assertTrue(optionalResourceDefinitionQuery.execute(
                GetResourceDefinitionDTO.create("resource-orders", "9.9.9")
        ).isEmpty());

        OptionalResourceDefinitionVersionQuery optionalVersionQuery =
                new OptionalResourceDefinitionVersionQuery(dataSource, jsonConfig.jsonMapper());
        Optional<ResourceDefinition> optionalDefinition = optionalVersionQuery.execute(
                GetResourceDefinitionDTO.create("resource-orders", "0.0.1")
        );
        Assertions.assertTrue(optionalDefinition.isPresent());
        Assertions.assertEquals("0.0.1", optionalDefinition.get().getVersion());
        Assertions.assertEquals(30, optionalDefinition.get().getConfig().get("lifecycleDays"));
        Assertions.assertTrue(optionalVersionQuery.execute(
                GetResourceDefinitionDTO.create("resource-orders", "9.9.9")
        ).isEmpty());

        GetResourceDefinitionQuery getResourceDefinitionQuery =
                new GetResourceDefinitionQuery(dataSource, jsonConfig.jsonMapper());
        ResourceDefinition activeDefinition = getResourceDefinitionQuery.execute(Key.create("resource-orders"));
        Assertions.assertEquals("0.0.1", activeDefinition.getVersion());
    }

    @Test
    void queriesMapExpectedFailureModes() throws Exception {
        DataSource initializedDataSource = initializedDataSource();
        seedSalesGraph(initializedDataSource);

        DataSource brokenDataSource = H2TestSupport.newDataSource();
        Assertions.assertThrows(DependencyException.class, () ->
                new CountResourceDefinitionsByResourceIdQuery(brokenDataSource).execute(Key.create("resource-orders"))
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new AllResourcesQuery(brokenDataSource).execute("sales")
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new OptionalResourceQuery(brokenDataSource).execute(GetResourceDTO.create("orders", "dp-sales"))
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new OptionalResourceDefinitionQuery(brokenDataSource).execute(
                        GetResourceDefinitionDTO.create("resource-orders", "0.0.1")
                )
        );

        Assertions.assertThrows(NotFoundException.class, () ->
                new GetResourceDefinitionQuery(initializedDataSource, jsonConfig.jsonMapper()).execute(Key.create("resource-customers"))
        );

        ObjectMapper failingMapper = new ObjectMapper() {
            @Override
            public <T> T readValue(String content, Class<T> valueType) throws JacksonException {
                throw new JacksonException("boom") { };
            }
        };
        Assertions.assertThrows(MappingException.class, () ->
                new OptionalResourceDefinitionVersionQuery(initializedDataSource, failingMapper).execute(
                        GetResourceDefinitionDTO.create("resource-orders", "0.0.1")
                )
        );
        Assertions.assertThrows(MappingException.class, () ->
                new GetResourceDefinitionQuery(initializedDataSource, failingMapper).execute(Key.create("resource-orders"))
        );
    }

    @Test
    void queriesPropagateAdapterMappingErrorsForInvalidDatabaseValues() throws Exception {
        DataSource dataSource = initializedDataSource();
        H2TestSupport.insertDataProduct(dataSource, "dp-invalid-schema", "invalid-schema", "Invalid", "source-aligned", "domain", "description");
        H2TestSupport.executeSql(dataSource, "UPDATE data_product SET schema_version = 'invalid/v1' WHERE id = 'dp-invalid-schema'");
        Assertions.assertThrows(MappingException.class, () -> new OptionalDataProductQuery(dataSource).execute("invalid-schema"));

        H2TestSupport.insertDataProduct(dataSource, "dp-invalid-kind", "invalid-kind", "Invalid", "source-aligned", "domain", "description");
        H2TestSupport.executeSql(dataSource, "UPDATE data_product SET kind = 'invalid-kind' WHERE id = 'dp-invalid-kind'");
        Assertions.assertThrows(MappingException.class, () -> new OptionalDataProductQuery(dataSource).execute("invalid-kind"));

        H2TestSupport.insertDataProduct(dataSource, "dp-resource", "resource-dp", "Resource DP", "source-aligned", "domain", "description");
        H2TestSupport.insertResource(dataSource, "resource-invalid-kind", "dp-resource", "broken", "Broken", "invalid-kind");
        Assertions.assertThrows(MappingException.class, () ->
                new OptionalResourceQuery(dataSource).execute(GetResourceDTO.create("broken", "dp-resource"))
        );
        Assertions.assertThrows(MappingException.class, () ->
                new AllResourcesQuery(dataSource).execute("resource-dp")
        );

        H2TestSupport.insertResource(dataSource, "resource-invalid-definition", "dp-resource", "broken-definition", "Broken definition", "bucket");
        H2TestSupport.insertResourceDefinitionRaw(
                dataSource,
                "definition-invalid-schema",
                "resource-invalid-definition",
                "0.0.1",
                true,
                "{\"lifecycleDays\":30}"
        );
        H2TestSupport.executeSql(
                dataSource,
                "UPDATE resource_definition SET schema_version = 'invalid-schema' WHERE id = 'definition-invalid-schema'"
        );
        Assertions.assertThrows(MappingException.class, () ->
                new GetResourceDefinitionQuery(dataSource, jsonConfig.jsonMapper()).execute(Key.create("resource-invalid-definition"))
        );
    }

    private DataSource initializedDataSource() {
        DataSource dataSource = H2TestSupport.newDataSource();
        new ApplicationConfig(dataSource);
        return dataSource;
    }

    private void seedSalesGraph(DataSource dataSource) throws Exception {
        H2TestSupport.insertDataProduct(dataSource, "dp-sales", "sales", "Sales", "source-aligned", "domain", "description");
        H2TestSupport.insertResource(dataSource, "resource-orders", "dp-sales", "orders", "Orders", "bucket");
        H2TestSupport.insertResource(dataSource, "resource-customers", "dp-sales", "customers", "Customers", "bucket");
        H2TestSupport.insertResourceDefinition(
                dataSource,
                "definition-orders-active",
                "resource-orders",
                "0.0.1",
                true,
                java.util.Map.of("lifecycleDays", 30)
        );
        H2TestSupport.insertResourceDefinition(
                dataSource,
                "definition-orders-inactive",
                "resource-orders",
                "0.0.0",
                false,
                java.util.Map.of("lifecycleDays", 15)
        );
    }
}
