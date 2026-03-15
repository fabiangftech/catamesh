package dev.catamesh.integration.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.config.AppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class DataProductFacadeTest {

    private AppConfig appConfig;

    @BeforeEach
    void setUp() throws IOException {
        deleteDatabaseFiles();
        appConfig = new AppConfig();
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
    void testApplyUnsupportedActions() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        String updatedYaml = yaml.replace("description: this is a data product!", "description: updated data product description");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();

        dataProductFacade.apply(yaml);
        ApplyResult applyResult = dataProductFacade.apply(updatedYaml);

        Assertions.assertEquals(ApplyStatus.SUCCESS, applyResult.getStatus());
        Assertions.assertEquals(0, applyResult.getSummary().getExecuted());
        Assertions.assertTrue(
                applyResult.getSteps().stream().anyMatch(step ->
                        step.getAction().getValue().equals("update")
                        && step.getStatus() == ApplyStepStatus.SKIPPED
                        && "Action not supported by current apply pipeline".equals(step.getMessage())
                )
        );
    }

    @Test
    void testGet() {
        String yaml = appConfig.getFileFromResourceQuery().execute("examples/data-product.example.yaml");
        DataProductFacade dataProductFacade = appConfig.dataProductFacade();
        dataProductFacade.apply(yaml);
        DataProduct dataProduct = dataProductFacade.get("my-first-data-product");
        Assertions.assertNotNull(dataProduct);
        String json = appConfig.jsonMapper().writeValueAsString(dataProduct);
        Assertions.assertNotNull(json);
    }

    private void deleteDatabaseFiles() throws IOException {
        Path dbPath = Path.of("db-file-catamesh");
        if (!Files.exists(dbPath)) {
            return;
        }

        try (var paths = Files.walk(dbPath)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
