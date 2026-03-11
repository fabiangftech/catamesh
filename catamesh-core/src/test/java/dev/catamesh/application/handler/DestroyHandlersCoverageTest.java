package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.infrastructure.config.JSONConfig;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static dev.catamesh.support.TestModelFactory.bucketDefinition;
import static dev.catamesh.support.TestModelFactory.bucketResource;
import static dev.catamesh.support.TestModelFactory.dataProduct;
import static dev.catamesh.support.TestModelFactory.resource;

class DestroyHandlersCoverageTest {

    private final JSONConfig jsonConfig = new JSONConfig();
    private final ObjectMapper jsonMapper = jsonConfig.jsonMapper();

    @Test
    void validateDestroyDataProductSchemaHandlerAcceptsValidRequest() {
        ValidateDestroyDataProductSchemaHandler handler = new ValidateDestroyDataProductSchemaHandler(
                jsonConfig.dataProductSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(validDestroyRequest("sales", List.of(bucketResource("orders", "0.0.1"))));

        Assertions.assertDoesNotThrow(() -> handler.handle(context));
    }

    @Test
    void validateDestroyDataProductSchemaHandlerRejectsInvalidRequest() {
        ValidateDestroyDataProductSchemaHandler handler = new ValidateDestroyDataProductSchemaHandler(
                jsonConfig.dataProductSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProductWithoutDomain("sales"));

        SchemaException error = Assertions.assertThrows(SchemaException.class, () -> handler.handle(context));

        Assertions.assertEquals("Error in destroy request for data product with name=sales", error.getMessage());
        Assertions.assertFalse(error.getErrors().isEmpty());
    }

    @Test
    void validateDestroyResourceSchemaHandlerAcceptsValidResources() {
        ValidateDestroyResourceSchemaHandler handler = new ValidateDestroyResourceSchemaHandler(
                jsonConfig.resourceSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));

        Assertions.assertDoesNotThrow(() -> handler.handle(context));
    }

    @Test
    void validateDestroyResourceSchemaHandlerRejectsInvalidResource() {
        ValidateDestroyResourceSchemaHandler handler = new ValidateDestroyResourceSchemaHandler(
                jsonConfig.resourceSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct(
                "sales",
                List.of(resource("orders", ResourceKind.BUCKET, null))
        ));

        SchemaException error = Assertions.assertThrows(SchemaException.class, () -> handler.handle(context));

        Assertions.assertTrue(error.getMessage().contains("name=sales"));
        Assertions.assertFalse(error.getErrors().isEmpty());
    }

    @Test
    void validateDestroyBucketDefinitionSchemaHandlerAcceptsValidBucketDefinition() {
        ValidateDestroyBucketDefinitionSchemaHandler handler = new ValidateDestroyBucketDefinitionSchemaHandler(
                jsonConfig.bucketSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));

        Assertions.assertDoesNotThrow(() -> handler.handle(context));
    }

    @Test
    void validateDestroyBucketDefinitionSchemaHandlerRejectsInvalidBucketDefinition() {
        ValidateDestroyBucketDefinitionSchemaHandler handler = new ValidateDestroyBucketDefinitionSchemaHandler(
                jsonConfig.bucketSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(
                resource("orders", ResourceKind.BUCKET, bucketDefinition("bad-version", 0))
        )));

        SchemaException error = Assertions.assertThrows(SchemaException.class, () -> handler.handle(context));

        Assertions.assertTrue(error.getMessage().contains("resource=orders"));
        Assertions.assertFalse(error.getErrors().isEmpty());
    }

    @Test
    void validateDestroyBucketDefinitionSchemaHandlerIgnoresNonBucketResources() {
        ValidateDestroyBucketDefinitionSchemaHandler handler = new ValidateDestroyBucketDefinitionSchemaHandler(
                jsonConfig.bucketSchema(),
                jsonMapper
        );
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(
                resource("jobs", ResourceKind.FLINK, (ResourceDefinition) null)
        )));

        Assertions.assertDoesNotThrow(() -> handler.handle(context));
    }

    @Test
    void validateDestroyDefinitionVersionHandlerAcceptsRequestedVersions() {
        ValidateDestroyDefinitionVersionHandler handler = new ValidateDestroyDefinitionVersionHandler();
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));

        Assertions.assertDoesNotThrow(() -> handler.handle(context));
    }

    @Test
    void validateDestroyDefinitionVersionHandlerRejectsMissingVersions() {
        ValidateDestroyDefinitionVersionHandler handler = new ValidateDestroyDefinitionVersionHandler();
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of(
                resource("orders", ResourceKind.BUCKET, bucketDefinition("", 30)),
                resource("payments", ResourceKind.BUCKET, null)
        )));

        SchemaException error = Assertions.assertThrows(SchemaException.class, () -> handler.handle(context));

        Assertions.assertEquals("Error in destroy request resources", error.getMessage());
        Assertions.assertEquals(2, error.getErrors().size());
    }

    @Test
    void destroyResourceDefinitionHandlerDeletesOnlyMatchingDefinitionsInApplyMode() {
        List<GetResourceDefinitionDTO> deleted = new ArrayList<>();
        DestroyResourceDefinitionHandler handler = new DestroyResourceDefinitionHandler(dto -> {
            deleted.add(dto);
            return null;
        });
        DestroyDataProductContext context = DestroyDataProductContext.createForApply("yaml");
        context.setDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));
        context.setPlan(plan(
                PlanResource.resourceDefinition("orders", "0.0.1", PlanAction.DELETE),
                PlanResource.resourceDefinition("orders", "0.0.2", PlanAction.NOOP),
                PlanResource.resourceDefinition("missing", "0.0.1", PlanAction.DELETE)
        ));

        handler.handle(context);

        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("0.0.1", deleted.get(0).version());
    }

    @Test
    void destroyResourceDefinitionHandlerSkipsPlanMode() {
        List<GetResourceDefinitionDTO> deleted = new ArrayList<>();
        DestroyResourceDefinitionHandler handler = new DestroyResourceDefinitionHandler(dto -> {
            deleted.add(dto);
            return null;
        });
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));
        context.setPlan(plan(PlanResource.resourceDefinition("orders", "0.0.1", PlanAction.DELETE)));

        handler.handle(context);

        Assertions.assertTrue(deleted.isEmpty());
    }

    @Test
    void destroyResourceHandlerDeletesOnlyMatchingResourcesInApplyMode() {
        List<String> deleted = new ArrayList<>();
        DestroyResourceHandler handler = new DestroyResourceHandler(key -> {
            deleted.add(key.value());
            return null;
        });
        DestroyDataProductContext context = DestroyDataProductContext.createForApply("yaml");
        context.setDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));
        context.setPlan(plan(
                PlanResource.resource("orders", PlanAction.DELETE),
                PlanResource.resource("payments", PlanAction.NOOP),
                PlanResource.resource("missing", PlanAction.DELETE)
        ));

        handler.handle(context);

        Assertions.assertEquals(List.of("resource-orders"), deleted);
    }

    @Test
    void destroyResourceHandlerSkipsPlanMode() {
        List<String> deleted = new ArrayList<>();
        DestroyResourceHandler handler = new DestroyResourceHandler(key -> {
            deleted.add(key.value());
            return null;
        });
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");
        context.setDataProduct(dataProduct("sales", List.of(bucketResource("orders", "0.0.1"))));
        context.setPlan(plan(PlanResource.resource("orders", PlanAction.DELETE)));

        handler.handle(context);

        Assertions.assertTrue(deleted.isEmpty());
    }

    @Test
    void destroyDataProductHandlerDeletesOnlyWhenApplyDeleteAndNoResourcesRemain() {
        List<String> deletedNames = new ArrayList<>();
        Query<String, List<Resource>> emptyResources = name -> List.of();
        DestroyDataProductHandler handler = new DestroyDataProductHandler(name -> {
            deletedNames.add(name);
            return null;
        }, emptyResources);

        DestroyDataProductContext context = DestroyDataProductContext.createForApply("yaml");
        context.setRequestDataProduct(dataProduct("sales", List.of()));
        context.setPlan(plan(PlanResource.resource("orders", PlanAction.DELETE)));
        context.getPlan().setAction(PlanAction.DELETE);

        handler.handle(context);

        Assertions.assertEquals(List.of("sales"), deletedNames);
    }

    @Test
    void destroyDataProductHandlerSkipsWhenModePlanActionOrResourcesPreventDeletion() {
        List<String> deletedNames = new ArrayList<>();
        Query<String, List<Resource>> currentResources = name -> List.of(bucketResource("orders", "0.0.1"));
        Command<String, Void> deleteDataProductCommand = name -> {
            deletedNames.add(name);
            return null;
        };
        DestroyDataProductHandler handler = new DestroyDataProductHandler(deleteDataProductCommand, currentResources);

        DestroyDataProductContext planContext = DestroyDataProductContext.createForPlan("yaml");
        planContext.setRequestDataProduct(dataProduct("sales", List.of()));
        planContext.setPlan(plan());
        planContext.getPlan().setAction(PlanAction.DELETE);
        handler.handle(planContext);

        DestroyDataProductContext noopContext = DestroyDataProductContext.createForApply("yaml");
        noopContext.setRequestDataProduct(dataProduct("sales", List.of()));
        noopContext.setPlan(plan());
        noopContext.getPlan().setAction(PlanAction.NOOP);
        handler.handle(noopContext);

        DestroyDataProductContext remainingResourcesContext = DestroyDataProductContext.createForApply("yaml");
        remainingResourcesContext.setRequestDataProduct(dataProduct("sales", List.of()));
        remainingResourcesContext.setPlan(plan());
        remainingResourcesContext.getPlan().setAction(PlanAction.DELETE);
        handler.handle(remainingResourcesContext);

        Assertions.assertTrue(deletedNames.isEmpty());
    }

    private Plan plan(PlanResource... resources) {
        Plan plan = new Plan("sales");
        for (PlanResource resource : resources) {
            plan.addResource(resource);
        }
        return plan;
    }

    private dev.catamesh.core.model.DataProduct validDestroyRequest(String name, List<Resource> resources) {
        return new dev.catamesh.core.model.DataProduct(
                dev.catamesh.core.model.SchemaVersion.DATA_PRODUCT_V1,
                new dev.catamesh.core.model.Metadata(name, "Display " + name, "domain", "description"),
                new dev.catamesh.core.model.Spec(dev.catamesh.core.model.DataProductKind.SOURCE_ALIGNED, resources)
        );
    }

    private dev.catamesh.core.model.DataProduct dataProductWithoutDomain(String name) {
        return new dev.catamesh.core.model.DataProduct(
                dev.catamesh.core.model.SchemaVersion.DATA_PRODUCT_V1,
                new dev.catamesh.core.model.Metadata(name, "Display " + name, null, "description"),
                new dev.catamesh.core.model.Spec(dev.catamesh.core.model.DataProductKind.SOURCE_ALIGNED, List.of())
        );
    }
}
