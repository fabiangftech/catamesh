package dev.catamesh.application.facade;

import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.support.H2TestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultDataProductFacadeDestroyFlowTest {
    private static final String DATA_PRODUCT_NAME = "my-first-data-product";

    @Test
    void planDestroyDeletesWholeDataProductWhenLastResourceIsRequested() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String yaml = singleResourceYaml("my-first-component", "My First component", "0.0.1", 30);

        facade.apply(yaml);

        Plan plan = facade.planDestroy(yaml);

        Assertions.assertEquals(PlanAction.DELETE, plan.getAction());
        Assertions.assertEquals(2, plan.getResources().size());
        Assertions.assertEquals(PlanAction.DELETE, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanAction.DELETE, plan.getResources().get(1).getAction());
        Assertions.assertEquals(3, plan.getSummary().getDelete());

        facade.applyDestroy(yaml);

        Assertions.assertThrows(NotFoundException.class, () -> facade.get(DATA_PRODUCT_NAME));
    }

    @Test
    void planDestroyReturnsNoopWhenRequestedVersionDoesNotExist() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();

        facade.apply(singleResourceYaml("my-first-component", "My First component", "0.0.1", 30));

        Plan plan = facade.planDestroy(singleResourceYaml("my-first-component", "My First component", "9.9.9", 30));

        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(2, plan.getResources().size());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(1).getAction());
        Assertions.assertEquals(3, plan.getSummary().getNoop());
    }

    @Test
    void applyDestroyDeletesRequestedResourceAndKeepsDataProductWhenOtherResourcesRemain() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String appliedYaml = twoResourceYaml();
        String destroyYaml = singleResourceYaml("my-first-component", "My First component", "0.0.1", 30);

        facade.apply(appliedYaml);

        Plan plan = facade.planDestroy(destroyYaml);

        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(2, plan.getResources().size());
        Assertions.assertEquals(2, plan.getSummary().getDelete());
        Assertions.assertEquals(1, plan.getSummary().getNoop());

        facade.applyDestroy(destroyYaml);

        DataProduct current = facade.get(DATA_PRODUCT_NAME);
        Assertions.assertEquals(1, current.getSpec().getResources().size());
        Assertions.assertEquals("second-component", current.getSpec().getResources().get(0).getName());
        Assertions.assertEquals("0.0.1", current.getSpec().getResources().get(0).getDefinition().getVersion());
    }

    @Test
    void planDestroyBuildsNoopPlanWhenCurrentDataProductDoesNotExist() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();

        Plan plan = facade.planDestroy(singleResourceYaml("ghost-resource", "Ghost resource", "0.0.1", 30));

        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(2, plan.getResources().size());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(1).getAction());
        Assertions.assertEquals(3, plan.getSummary().getNoop());
    }

    @Test
    void planDestroyRejectsMissingDefinitionVersion() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String invalidYaml = """
                schemaVersion: data-product/v1
                metadata:
                  name: my-first-data-product
                  displayName: Test
                  domain: test
                  description: destroy coverage
                spec:
                  kind: source-aligned
                  resources:
                    -
                      name: my-first-component
                      displayName: My First component
                      kind: flink
                      definition:
                        schemaVersion: bucket/v1
                        version: ""
                        config:
                          lifecycleDays: 30
                """;

        SchemaException exception = Assertions.assertThrows(SchemaException.class, () -> facade.planDestroy(invalidYaml));

        Assertions.assertTrue(exception.getErrors().contains("resource=my-first-component requires definition.version for destroy"));
    }

    @Test
    void planDestroyRejectsInvalidResourceSchema() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String invalidYaml = singleResourceYaml("INVALID_NAME", "My First component", "0.0.1", 30);

        Assertions.assertThrows(SchemaException.class, () -> facade.planDestroy(invalidYaml));
    }

    @Test
    void planDestroyRejectsInvalidBucketDefinitionSchema() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String invalidYaml = singleResourceYaml("my-first-component", "My First component", "0.0.1", 0);

        Assertions.assertThrows(SchemaException.class, () -> facade.planDestroy(invalidYaml));
    }

    @Test
    void planDestroyRejectsInvalidDataProductSchema() {
        ApplicationConfig applicationConfig = H2TestSupport.newApplicationConfig();
        DataProductFacade facade = applicationConfig.dataProductFacade();
        String invalidYaml = """
                schemaVersion: data-product/v1
                metadata:
                  displayName: Test
                spec:
                  kind: source-aligned
                """;

        Assertions.assertThrows(SchemaException.class, () -> facade.planDestroy(invalidYaml));
    }

    private String singleResourceYaml(String resourceName,
                                      String displayName,
                                      String version,
                                      int lifecycleDays) {
        return """
                schemaVersion: data-product/v1
                metadata:
                  name: my-first-data-product
                  displayName: Test
                  domain: test
                  description: destroy coverage
                spec:
                  kind: source-aligned
                  resources:
                    -
                      name: %s
                      displayName: %s
                      kind: bucket
                      definition:
                        schemaVersion: bucket/v1
                        version: %s
                        config:
                          lifecycleDays: %s
                """.formatted(resourceName, displayName, version, lifecycleDays);
    }

    private String twoResourceYaml() {
        return """
                schemaVersion: data-product/v1
                metadata:
                  name: my-first-data-product
                  displayName: Test
                  domain: test
                  description: destroy coverage
                spec:
                  kind: source-aligned
                  resources:
                    -
                      name: my-first-component
                      displayName: My First component
                      kind: bucket
                      definition:
                        schemaVersion: bucket/v1
                        version: 0.0.1
                        config:
                          lifecycleDays: 30
                    -
                      name: second-component
                      displayName: Second component
                      kind: bucket
                      definition:
                        schemaVersion: bucket/v1
                        version: 0.0.1
                        config:
                          lifecycleDays: 15
                """;
    }
}
