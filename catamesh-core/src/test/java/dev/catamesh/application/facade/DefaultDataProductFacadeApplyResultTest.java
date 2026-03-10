package dev.catamesh.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class DefaultDataProductFacadeApplyResultTest {

    @Test
    void applyReturnsCreatePlanAndPersistedDataProduct() throws Exception {
        ApplicationConfig applicationConfig = newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String yaml = applicationConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");

        ApplyResult result = facade.apply(yaml);

        Assertions.assertEquals(PlanAction.CREATE, result.getPlan().getAction());
        Assertions.assertNotNull(result.getPlan().getRequestId());
        Assertions.assertNotNull(result.getDataProduct().getMetadata().getId());
        Assertions.assertEquals("my-first-data-product", result.getDataProduct().getMetadata().getName());
        Assertions.assertEquals(1, result.getDataProduct().getSpec().getResources().size());
        Assertions.assertNotNull(result.getDataProduct().getSpec().getResources().get(0).getId());
        Assertions.assertNotNull(result.getDataProduct().getSpec().getResources().get(0).getDataProductId());
        Assertions.assertEquals("0.0.1", result.getDataProduct().getSpec().getResources().get(0).getDefinition().getVersion());
        Assertions.assertTrue(applicationConfig.jsonMapper().writeValueAsString(result).contains("\"dataProduct\""));
    }

    @Test
    void applySameYamlReturnsNoopAndMatchesCurrentState() {
        ApplicationConfig applicationConfig = newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String yaml = applicationConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");

        facade.apply(yaml);
        ApplyResult result = facade.apply(yaml);
        DataProduct current = facade.get("my-first-data-product");

        Assertions.assertEquals(PlanAction.NOOP, result.getPlan().getAction());
        Assertions.assertEquals(current.getMetadata().getId(), result.getDataProduct().getMetadata().getId());
        Assertions.assertEquals(
                current.getSpec().getResources().get(0).getDefinition().getVersion(),
                result.getDataProduct().getSpec().getResources().get(0).getDefinition().getVersion()
        );
    }

    @Test
    void applyNewDefinitionVersionReturnsFinalStateWithNewActiveVersion() {
        ApplicationConfig applicationConfig = newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String yaml = applicationConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        String yamlNewVersion = yaml.replace("version: 0.0.1", "version: 0.0.2");

        facade.apply(yaml);
        ApplyResult result = facade.apply(yamlNewVersion);

        Assertions.assertEquals(PlanAction.NOOP, result.getPlan().getAction());
        Assertions.assertEquals(1, result.getPlan().getSummary().getCreate());
        Assertions.assertEquals("0.0.2", result.getDataProduct().getSpec().getResources().get(0).getDefinition().getVersion());
    }

    @Test
    void applyUpdateLikeReturnsPlanButKeepsPersistedStateImplementedToday() {
        ApplicationConfig applicationConfig = newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String yaml = applicationConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        String yamlWithMetadataChange = yaml
                .replace("displayName: Test", "displayName: Test Updated")
                .replace("description: this is a data product!", "description: updated description");

        facade.apply(yaml);
        ApplyResult result = facade.apply(yamlWithMetadataChange);

        Assertions.assertEquals(PlanAction.UPDATE, result.getPlan().getAction());
        Assertions.assertTrue(result.getPlan().getSummary().getUpdate() >= 1);
        Assertions.assertEquals("Test", result.getDataProduct().getMetadata().getDisplayName());
        Assertions.assertEquals("this is a data product!", result.getDataProduct().getMetadata().getDescription());
    }

    private ApplicationConfig newApplicationConfig() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUser("catamesh");
        dataSource.setPassword("catamesh");
        return new ApplicationConfig(dataSource);
    }
}
