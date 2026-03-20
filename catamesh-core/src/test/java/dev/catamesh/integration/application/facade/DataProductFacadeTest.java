package dev.catamesh.integration.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.config.AppConfig;
import dev.catamesh.infrastructure.config.JSONConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

class DataProductFacadeTest {
    @TempDir
    Path tempDir;

    private AppConfig appConfig;
    private String previousDbDirProperty;

    @BeforeEach
    void setUp() {
        previousDbDirProperty = System.getProperty(AppConfig.DB_DIR_SYSTEM_PROPERTY);
        System.setProperty(AppConfig.DB_DIR_SYSTEM_PROPERTY, tempDir.resolve("db-file-catamesh").toString());
        appConfig = new AppConfig();
    }

    @AfterEach
    void tearDown() {
        if (previousDbDirProperty == null) {
            System.clearProperty(AppConfig.DB_DIR_SYSTEM_PROPERTY);
            return;
        }

        System.setProperty(AppConfig.DB_DIR_SYSTEM_PROPERTY, previousDbDirProperty);
    }

    @Test
    void testDiff() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();
        DiffResult diffResult = dataProductFacade.diff(yaml);
        Assertions.assertNotNull(diffResult);
        Assertions.assertNotNull(diffResult.getSummary());
    }

    @Test
    void testPlan() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();
        PlanResult planResult = dataProductFacade.plan(yaml);
        Assertions.assertNotNull(planResult);
        Assertions.assertNotNull(planResult.getSummary());
    }

    @Test
    void testApply() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();
        ApplyResult applyResult = dataProductFacade.apply(yaml);
        Assertions.assertNotNull(applyResult);
        Assertions.assertEquals(ApplyStatus.SUCCESS, applyResult.getStatus());
    }

    @Test
    void testApplyWithoutChanges() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();

        dataProductFacade.apply(yaml);
        ApplyResult applyResult = dataProductFacade.apply(yaml);

        Assertions.assertEquals(ApplyStatus.SUCCESS, applyResult.getStatus());
        Assertions.assertEquals(0, applyResult.getSummary().getExecuted());
        Assertions.assertEquals(4, applyResult.getSummary().getSkipped());
        Assertions.assertTrue(applyResult.getSteps().stream().allMatch(step -> step.getStatus() == ApplyStepStatus.SKIPPED));
        Assertions.assertTrue(applyResult.getSteps().stream().allMatch(step -> "No changes to apply".equals(step.getMessage())));
    }

    @Test
    void testGet() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();
        dataProductFacade.apply(yaml);
        DataProduct dataProduct = dataProductFacade.get("my-first-data-product");
        Assertions.assertNotNull(dataProduct);
        String json = JSONConfig.jsonMapper().writeValueAsString(dataProduct);
        Assertions.assertNotNull(json);
    }
}
