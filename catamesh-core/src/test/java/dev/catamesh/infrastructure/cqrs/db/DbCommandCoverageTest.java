package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.config.JSONConfig;
import dev.catamesh.support.H2TestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;

import static dev.catamesh.support.TestModelFactory.bucketResource;
import static dev.catamesh.support.TestModelFactory.dataProduct;

class DbCommandCoverageTest {

    private final JSONConfig jsonConfig = new JSONConfig();

    @Test
    void createUpdateDeleteAndDeactivateCommandsCoverHappyPathsAndInvariants() throws Exception {
        DataSource dataSource = H2TestSupport.newDataSource();
        new dev.catamesh.infrastructure.config.ApplicationConfig(dataSource);

        CreateDataProductCommand createDataProductCommand = new CreateDataProductCommand(dataSource);
        DataProduct createdDataProduct = createDataProductCommand.execute(dataProduct("sales", java.util.List.of()));
        Assertions.assertNotNull(createdDataProduct.getMetadata().getId());
        Assertions.assertEquals(1, H2TestSupport.countRows(dataSource, "data_product"));

        UpdateDataProductCommand updateDataProductCommand = new UpdateDataProductCommand(dataSource);
        createdDataProduct = new DataProduct(
                createdDataProduct.getSchemaVersion(),
                new dev.catamesh.core.model.Metadata(
                        Key.create(createdDataProduct.getMetadata().getId()),
                        createdDataProduct.getMetadata().getName(),
                        "Updated sales",
                        "updated-domain",
                        "updated-description"
                ),
                createdDataProduct.getSpec()
        );
        updateDataProductCommand.execute(createdDataProduct);

        OptionalDataProductQuery optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        DataProduct loadedDataProduct = optionalDataProductQuery.execute("sales").orElseThrow();
        Assertions.assertEquals("Updated sales", loadedDataProduct.getMetadata().getDisplayName());
        Assertions.assertEquals("updated-domain", loadedDataProduct.getMetadata().getDomain());
        Assertions.assertEquals("updated-description", loadedDataProduct.getMetadata().getDescription());

        Resource resource = bucketResource("orders", "0.0.1");
        resource.setDataProductId(Key.create(createdDataProduct.getMetadata().getId()));

        CreateResourceCommand createResourceCommand = new CreateResourceCommand(dataSource);
        createResourceCommand.execute(resource);
        Assertions.assertNotNull(resource.getId());
        Assertions.assertEquals(1, H2TestSupport.countRows(dataSource, "resource"));

        UpdateResourceCommand updateResourceCommand = new UpdateResourceCommand(dataSource);
        Resource updatedResource = bucketResource("orders", "0.0.1");
        updatedResource.setId(Key.create(resource.getId()));
        updatedResource.setDataProductId(Key.create(createdDataProduct.getMetadata().getId()));
        updatedResource = updateResourceCommand.execute(updatedResource);
        Assertions.assertEquals("Display orders", updatedResource.getDisplayName());

        Resource loadedResource = new OptionalResourceQuery(dataSource)
                .execute(dev.catamesh.infrastructure.dto.GetResourceDTO.create("orders", createdDataProduct.getMetadata().getId()))
                .orElseThrow();
        Assertions.assertEquals("Display orders", loadedResource.getDisplayName());

        CreateResourceDefinitionCommand createResourceDefinitionCommand = new CreateResourceDefinitionCommand(dataSource, jsonConfig.jsonMapper());
        createResourceDefinitionCommand.execute(resource);
        Assertions.assertEquals(1, H2TestSupport.countRows(dataSource, "resource_definition"));

        DeactivateResourceDefinitionsByResourceIdCommand deactivateCommand =
                new DeactivateResourceDefinitionsByResourceIdCommand(dataSource);
        deactivateCommand.execute(resource);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> new GetResourceDefinitionQuery(dataSource, jsonConfig.jsonMapper()).execute(resource.getKey())
        );
        Assertions.assertTrue(
                new OptionalResourceDefinitionQuery(dataSource).execute(
                        dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO.create(resource.getId(), "0.0.1")
                ).isPresent()
        );

        DeleteResourceDefinitionCommand deleteResourceDefinitionCommand = new DeleteResourceDefinitionCommand(dataSource);
        deleteResourceDefinitionCommand.execute(dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO.create(resource.getId(), "0.0.1"));
        Assertions.assertEquals(0, H2TestSupport.countRows(dataSource, "resource_definition"));

        DeleteResourceCommand deleteResourceCommand = new DeleteResourceCommand(dataSource);
        deleteResourceCommand.execute(resource.getKey());
        Assertions.assertEquals(0, H2TestSupport.countRows(dataSource, "resource"));

        DeleteDataProductCommand deleteDataProductCommand = new DeleteDataProductCommand(dataSource);
        deleteDataProductCommand.execute("sales");
        Assertions.assertEquals(0, H2TestSupport.countRows(dataSource, "data_product"));

        Assertions.assertThrows(InvariantException.class, () ->
                updateDataProductCommand.execute(new DataProduct(
                        dev.catamesh.core.model.SchemaVersion.DATA_PRODUCT_V1,
                        new dev.catamesh.core.model.Metadata("broken", "Broken", "domain", "description"),
                        new dev.catamesh.core.model.Spec(dev.catamesh.core.model.DataProductKind.SOURCE_ALIGNED, java.util.List.of())
                ))
        );
        Assertions.assertThrows(InvariantException.class, () ->
                updateResourceCommand.execute(new Resource(
                        null,
                        null,
                        "broken",
                        "Broken",
                        dev.catamesh.core.model.ResourceKind.BUCKET
                ))
        );
        Assertions.assertThrows(InvariantException.class, () ->
                deactivateCommand.execute(new Resource(
                        null,
                        null,
                        "broken-two",
                        "Broken two",
                        dev.catamesh.core.model.ResourceKind.BUCKET
                ))
        );
    }

    @Test
    void commandClassesMapDependencyAndMappingErrors() {
        DataSource dataSource = H2TestSupport.newDataSource();

        Assertions.assertThrows(DependencyException.class, () ->
                new CreateDataProductCommand(dataSource).execute(dataProduct("sales", java.util.List.of()))
        );

        Resource resource = bucketResource("orders", "0.0.1");
        resource.setDataProductId(Key.create("dp-sales"));

        Assertions.assertThrows(DependencyException.class, () ->
                new CreateResourceCommand(dataSource).execute(resource)
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new UpdateDataProductCommand(dataSource).execute(new DataProduct(
                        dev.catamesh.core.model.SchemaVersion.DATA_PRODUCT_V1,
                        new dev.catamesh.core.model.Metadata(Key.create("dp-sales"), "sales", "Display sales", "domain", "description"),
                        new dev.catamesh.core.model.Spec(dev.catamesh.core.model.DataProductKind.SOURCE_ALIGNED, java.util.List.of())
                ))
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new UpdateResourceCommand(dataSource).execute(resourceWithExistingId(resource))
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new DeleteDataProductCommand(dataSource).execute("sales")
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new DeleteResourceCommand(dataSource).execute(Key.create("resource-orders"))
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new DeleteResourceDefinitionCommand(dataSource).execute(
                        dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO.create("resource-orders", "0.0.1")
                )
        );
        Assertions.assertThrows(DependencyException.class, () ->
                new DeactivateResourceDefinitionsByResourceIdCommand(dataSource).execute(resourceWithExistingId(resource))
        );

        ObjectMapper failingMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JacksonException {
                throw new JacksonException("boom") { };
            }
        };
        DataSource initializedDataSource = H2TestSupport.newDataSource();
        new dev.catamesh.infrastructure.config.ApplicationConfig(initializedDataSource);
        Assertions.assertThrows(MappingException.class, () ->
                new CreateResourceDefinitionCommand(initializedDataSource, failingMapper)
                        .execute(resourceWithExistingId(bucketResource("mapped", "0.0.1")))
        );
    }

    private Resource resourceWithExistingId(Resource resource) {
        resource.setId(Key.create(resource.getId() == null ? "resource-" + resource.getName() : resource.getId()));
        resource.setDataProductId(Key.create("dp-sales"));
        return resource;
    }
}
