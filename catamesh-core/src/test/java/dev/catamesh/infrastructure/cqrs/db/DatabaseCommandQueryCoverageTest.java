package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.exception.AlreadyExistsException;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import dev.catamesh.infrastructure.config.JSONConfig;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import dev.catamesh.infrastructure.dto.GetResourceDTO;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import dev.catamesh.support.H2TestSupport;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DatabaseCommandQueryCoverageTest {
    private final ObjectMapper jsonMapper = new JSONConfig().jsonMapper();

    @Test
    void initTablesCreatesSchemaAndGetFileFromResourceReadsClasspathFiles() {
        JdbcDataSource dataSource = H2TestSupport.newDataSource();
        InitTablesDBCommand initTablesDBCommand = new InitTablesDBCommand(dataSource, new GetFileFromResourceQuery());
        GetFileFromResourceQuery getFileFromResourceQuery = new GetFileFromResourceQuery();

        initTablesDBCommand.execute(null);

        Assertions.assertEquals(
                3,
                H2TestSupport.queryInt(
                        dataSource,
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"
                )
        );
        Assertions.assertTrue(getFileFromResourceQuery.execute("examples/data-product.example.yaml").contains("schemaVersion"));
        Assertions.assertThrows(NotFoundException.class, () -> getFileFromResourceQuery.execute("missing.file"));
    }

    @Test
    void initTablesWrapsDuplicateKeysAndUnexpectedFailures() {
        JdbcDataSource duplicateDataSource = H2TestSupport.newDataSource();
        InitTablesDBCommand duplicateInit = new InitTablesDBCommand(
                duplicateDataSource,
                path -> """
                        CREATE TABLE duplicate_resource (
                            data_product_id VARCHAR(20) NOT NULL,
                            name VARCHAR(20) NOT NULL,
                            CONSTRAINT ux_duplicate_resource UNIQUE (data_product_id, name)
                        );
                        INSERT INTO duplicate_resource (data_product_id, name) VALUES ('dp-1', 'orders');
                        INSERT INTO duplicate_resource (data_product_id, name) VALUES ('dp-1', 'orders');
                        """
        );
        InitTablesDBCommand failingInit = new InitTablesDBCommand(
                H2TestSupport.newDataSource(),
                path -> {
                    throw new IllegalStateException("boom");
                }
        );

        Assertions.assertThrows(AlreadyExistsException.class, () -> duplicateInit.execute(null));
        Assertions.assertThrows(DependencyException.class, () -> failingInit.execute(null));
    }

    @Test
    void queriesReadCurrentDatabaseState() {
        DataSource dataSource = preparedDataSource();
        OptionalDataProductQuery optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        OptionalResourceQuery optionalResourceQuery = new OptionalResourceQuery(dataSource);
        OptionalResourceDefinitionQuery optionalResourceDefinitionQuery = new OptionalResourceDefinitionQuery(dataSource);
        CountResourceDefinitionsByResourceIdQuery countResourceDefinitionsByResourceIdQuery =
                new CountResourceDefinitionsByResourceIdQuery(dataSource);
        OptionalResourceDefinitionVersionQuery optionalResourceDefinitionVersionQuery =
                new OptionalResourceDefinitionVersionQuery(dataSource, jsonMapper);
        GetResourceDefinitionQuery getResourceDefinitionQuery = new GetResourceDefinitionQuery(dataSource, jsonMapper);

        Assertions.assertTrue(optionalDataProductQuery.execute("sales-dp").isPresent());
        Assertions.assertTrue(optionalDataProductQuery.execute("missing-dp").isEmpty());
        Assertions.assertTrue(optionalResourceQuery.execute(GetResourceDTO.create("orders", "dp-1")).isPresent());
        Assertions.assertTrue(optionalResourceQuery.execute(GetResourceDTO.create("missing", "dp-1")).isEmpty());
        Assertions.assertTrue(optionalResourceDefinitionQuery.execute(GetResourceDefinitionDTO.create("resource-1", "0.0.2")).isPresent());
        Assertions.assertTrue(optionalResourceDefinitionQuery.execute(GetResourceDefinitionDTO.create("resource-1", "9.9.9")).isEmpty());
        Assertions.assertEquals(2, countResourceDefinitionsByResourceIdQuery.execute(Key.create("resource-1")));

        Optional<ResourceDefinition> optionalDefinition =
                optionalResourceDefinitionVersionQuery.execute(GetResourceDefinitionDTO.create("resource-1", "0.0.2"));
        Assertions.assertTrue(optionalDefinition.isPresent());
        Assertions.assertEquals(SchemaVersion.BUCKET_V1, optionalDefinition.get().getSchemaVersion());
        Assertions.assertEquals("0.0.2", optionalDefinition.get().getVersion());
        Assertions.assertEquals(90, optionalDefinition.get().getConfig().get("lifecycleDays"));
        Assertions.assertTrue(
                optionalResourceDefinitionVersionQuery.execute(GetResourceDefinitionDTO.create("resource-1", "9.9.9")).isEmpty()
        );

        ResourceDefinition activeDefinition = getResourceDefinitionQuery.execute(Key.create("resource-1"));
        Assertions.assertEquals("0.0.2", activeDefinition.getVersion());
        Assertions.assertThrows(NotFoundException.class, () -> getResourceDefinitionQuery.execute(Key.create("missing-resource")));
    }

    @Test
    void commandsPersistUpdatesDeletesAndValidateRequiredIds() {
        DataSource dataSource = preparedDataSource();
        UpdateDataProductCommand updateDataProductCommand = new UpdateDataProductCommand(dataSource);
        UpdateResourceCommand updateResourceCommand = new UpdateResourceCommand(dataSource);
        DeleteResourceDefinitionCommand deleteResourceDefinitionCommand = new DeleteResourceDefinitionCommand(dataSource);
        DeleteResourceCommand deleteResourceCommand = new DeleteResourceCommand(dataSource);
        DeleteDataProductCommand deleteDataProductCommand = new DeleteDataProductCommand(dataSource);

        updateDataProductCommand.execute(dataProductWithId("dp-1", "sales-dp", "Sales Updated", "Updated description"));
        updateResourceCommand.execute(resourceWithId("resource-1", "orders", "Orders Updated", "0.0.2", 90));

        Assertions.assertEquals(
                "Sales Updated",
                H2TestSupport.queryString(dataSource, "SELECT display_name FROM data_product WHERE id = ?", "dp-1")
        );
        Assertions.assertEquals(
                "Orders Updated",
                H2TestSupport.queryString(dataSource, "SELECT display_name FROM resource WHERE id = ?", "resource-1")
        );

        Assertions.assertThrows(InvariantException.class, () ->
                updateDataProductCommand.execute(dataProductWithoutId("sales-dp"))
        );
        Assertions.assertThrows(InvariantException.class, () ->
                updateResourceCommand.execute(resourceWithoutId("orders"))
        );

        deleteResourceDefinitionCommand.execute(GetResourceDefinitionDTO.create("resource-1", "0.0.2"));
        Assertions.assertEquals(1, H2TestSupport.countRows(dataSource, "resource_definition"));

        deleteResourceCommand.execute(Key.create("resource-1"));
        Assertions.assertEquals(0, H2TestSupport.countRows(dataSource, "resource"));

        deleteDataProductCommand.execute("sales-dp");
        Assertions.assertEquals(0, H2TestSupport.countRows(dataSource, "data_product"));
    }

    @Test
    void createResourceDefinitionCommandPersistsJsonConfigInH2() {
        DataSource dataSource = H2TestSupport.newDataSource();
        H2TestSupport.initSchema(dataSource);
        H2TestSupport.insertDataProduct(
                dataSource,
                "dp-1",
                "data-product/v1",
                "sales-dp",
                "Sales",
                "source-aligned",
                "sales",
                "Sales data product"
        );
        H2TestSupport.insertResource(dataSource, "resource-1", "dp-1", "orders", "Orders", "bucket");

        CreateResourceDefinitionCommand command = new CreateResourceDefinitionCommand(dataSource, jsonMapper);
        Resource resource = resourceWithId("resource-1", "orders", "Orders", "0.0.3", 120);

        command.execute(resource);

        Assertions.assertEquals(1, H2TestSupport.countRows(dataSource, "resource_definition"));
        Assertions.assertTrue(
                H2TestSupport.queryString(
                        dataSource,
                        "SELECT config FROM resource_definition WHERE resource_id = ?",
                        "resource-1"
                ).contains("\"lifecycleDays\":120")
        );
    }

    private DataSource preparedDataSource() {
        DataSource dataSource = H2TestSupport.newDataSource();
        H2TestSupport.initSchema(dataSource);
        H2TestSupport.insertDataProduct(
                dataSource,
                "dp-1",
                "data-product/v1",
                "sales-dp",
                "Sales",
                "source-aligned",
                "sales",
                "Sales data product"
        );
        H2TestSupport.insertResource(dataSource, "resource-1", "dp-1", "orders", "Orders", "bucket");
        H2TestSupport.insertResourceDefinition(
                dataSource,
                "definition-1",
                "resource-1",
                "bucket/v1",
                "0.0.1",
                false,
                "{\"lifecycleDays\":30}"
        );
        H2TestSupport.insertResourceDefinition(
                dataSource,
                "definition-2",
                "resource-1",
                "bucket/v1",
                "0.0.2",
                true,
                "{\"lifecycleDays\":90}"
        );
        return dataSource;
    }

    private DataProduct dataProductWithId(String id, String name, String displayName, String description) {
        Metadata metadata = new Metadata(name, displayName, "sales", description);
        metadata.setId(id);
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                metadata,
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of())
        );
    }

    private DataProduct dataProductWithoutId(String name) {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata(name, "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of())
        );
    }

    private Resource resourceWithId(String id,
                                    String name,
                                    String displayName,
                                    String version,
                                    int lifecycleDays) {
        Resource resource = new Resource(
                name,
                displayName,
                ResourceKind.BUCKET,
                new ResourceDefinition(SchemaVersion.BUCKET_V1, version, Map.of("lifecycleDays", lifecycleDays))
        );
        resource.setId(Key.create(id));
        return resource;
    }

    private Resource resourceWithoutId(String name) {
        return new Resource(
                null,
                null,
                name,
                "Orders",
                ResourceKind.BUCKET
        );
    }
}
