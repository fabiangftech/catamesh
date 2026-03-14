package dev.catamesh.integration.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.infrastructure.config.AppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataProductFacadeTest {

    private final AppConfig appConfig = new AppConfig();

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
    }
}
