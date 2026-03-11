package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class PlanDestroyDataProductHandlerTest {

    @Test
    void handleDeduplicatesRequestedDefinitionVersionsAndPreservesOrder() {
        Query<GetResourceDefinitionDTO, Optional<String>> optionalDefinitionQuery = dto ->
                Optional.of("orders".equals(dto.resourceId()) ? dto.version() : "missing");
        Query<Key, Integer> countDefinitionsQuery = key -> 2;

        PlanDestroyDataProductHandler handler = new PlanDestroyDataProductHandler(
                optionalDefinitionQuery,
                countDefinitionsQuery
        );

        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("destroy-yaml");
        context.setRequestDataProduct(requestedDestroyDataProduct());
        context.setDataProduct(currentDataProduct());

        handler.handle(context);

        Plan plan = context.getPlan();
        List<PlanResource> resources = plan.getResources();

        Assertions.assertEquals(3, resources.size());
        Assertions.assertEquals("orders", resources.get(0).getName());
        Assertions.assertEquals("0.0.2", resources.get(0).getVersion());
        Assertions.assertEquals(PlanAction.DELETE, resources.get(0).getAction());
        Assertions.assertEquals("0.0.1", resources.get(1).getVersion());
        Assertions.assertEquals(PlanAction.DELETE, resources.get(1).getAction());
        Assertions.assertEquals(PlanAction.DELETE, resources.get(2).getAction());
        Assertions.assertEquals(PlanAction.DELETE, plan.getAction());
        Assertions.assertEquals(4, plan.getSummary().getDelete());
    }

    @Test
    void handleMarksMissingResourcesAsNoop() {
        Query<GetResourceDefinitionDTO, Optional<String>> optionalDefinitionQuery = dto -> Optional.empty();
        Query<Key, Integer> countDefinitionsQuery = key -> 0;

        PlanDestroyDataProductHandler handler = new PlanDestroyDataProductHandler(
                optionalDefinitionQuery,
                countDefinitionsQuery
        );

        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("destroy-yaml");
        context.setRequestDataProduct(new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource("missing", "0.0.1")))
        ));
        context.setDataProduct(currentDataProduct());

        handler.handle(context);

        Plan plan = context.getPlan();
        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(0, plan.getSummary().getDelete());
        Assertions.assertEquals(3, plan.getSummary().getNoop());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(1).getAction());
    }

    @Test
    void handleKeepsResourceWhenOnlySomeDefinitionsAreDeleted() {
        Query<GetResourceDefinitionDTO, Optional<String>> optionalDefinitionQuery = dto ->
                "0.0.2".equals(dto.version()) ? Optional.of("found") : Optional.empty();
        Query<Key, Integer> countDefinitionsQuery = key -> 2;

        PlanDestroyDataProductHandler handler = new PlanDestroyDataProductHandler(
                optionalDefinitionQuery,
                countDefinitionsQuery
        );

        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("destroy-yaml");
        context.setRequestDataProduct(new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource("orders", "0.0.2")))
        ));
        context.setDataProduct(currentDataProduct());

        handler.handle(context);

        Plan plan = context.getPlan();
        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(1, plan.getSummary().getDelete());
        Assertions.assertEquals(2, plan.getSummary().getNoop());
        Assertions.assertEquals(PlanAction.DELETE, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(1).getAction());
    }

    @Test
    void handleMarksPlanNoopWhenNoDefinitionExists() {
        Query<GetResourceDefinitionDTO, Optional<String>> optionalDefinitionQuery = dto -> Optional.empty();
        Query<Key, Integer> countDefinitionsQuery = key -> 1;

        PlanDestroyDataProductHandler handler = new PlanDestroyDataProductHandler(
                optionalDefinitionQuery,
                countDefinitionsQuery
        );

        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("destroy-yaml");
        context.setRequestDataProduct(new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource("orders", "9.9.9")))
        ));
        context.setDataProduct(currentDataProduct());

        handler.handle(context);

        Plan plan = context.getPlan();
        Assertions.assertEquals(PlanAction.NOOP, plan.getAction());
        Assertions.assertEquals(0, plan.getSummary().getDelete());
        Assertions.assertEquals(3, plan.getSummary().getNoop());
        Assertions.assertTrue(plan.getResources().stream().allMatch(resource -> resource.getAction().equals(PlanAction.NOOP)));
    }

    private DataProduct requestedDestroyDataProduct() {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(
                        DataProductKind.SOURCE_ALIGNED,
                        List.of(
                                resource("orders", "0.0.2"),
                                resource("orders", "0.0.2"),
                                resource("orders", "0.0.1")
                        )
                )
        );
    }

    private DataProduct currentDataProduct() {
        Resource currentResource = resource("orders", "0.0.1");
        currentResource.setId(Key.create("orders"));

        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(currentResource))
        );
    }

    private Resource resource(String name, String version) {
        return new Resource(
                name,
                name,
                ResourceKind.BUCKET,
                new ResourceDefinition(SchemaVersion.BUCKET_V1, version, Map.of("lifecycleDays", 30))
        );
    }
}
