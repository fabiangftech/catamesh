package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import dev.catamesh.infrastructure.dto.GetResourceDTO;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class PlanCheckResourceDefinitionVersionHandlerTest {

    @Test
    void doHandleAddsCreateDefinitionPlanWhenVersionDoesNotExist() {
        Query<GetResourceDefinitionDTO, Optional<String>> optionalDefinitionQuery = dto -> Optional.empty();
        Query<GetResourceDTO, Optional<Resource>> optionalResourceQuery = dto -> Optional.of(existingResource("resource-1"));
        PlanCheckResourceDefinitionVersionHandler handler = new PlanCheckResourceDefinitionVersionHandler(
                optionalDefinitionQuery,
                optionalResourceQuery
        );

        ApplyDataProductContext context = ApplyDataProductContext.create("yaml");
        context.setDataProduct(dataProduct(resource("orders", "0.0.2")));
        context.setPlan(planWithResourceAction("orders", PlanAction.NOOP));

        handler.handle(context);

        Assertions.assertEquals("resource-1", context.getResources().get(0).getId());
        PlanResource planResource = context.getPlan().getResources().get(1);
        Assertions.assertEquals(PlanAction.CREATE, planResource.getAction());
        Assertions.assertEquals(1, context.getPlan().getSummary().getCreate());
    }

    private Plan planWithResourceAction(String resourceName, PlanAction action) {
        Plan plan = new Plan("sales-dp");
        plan.addResource(PlanResource.resource(resourceName, action));
        return plan;
    }

    private DataProduct dataProduct(Resource resource) {
        Metadata metadata = new Metadata("sales-dp", "Sales", "sales", "Sales data product");
        metadata.setId("data-product-1");
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                metadata,
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource))
        );
    }

    private Resource resource(String name, String version) {
        Resource resource = new Resource(
                name,
                name,
                ResourceKind.BUCKET,
                new ResourceDefinition(SchemaVersion.BUCKET_V1, version, Map.of("lifecycleDays", 30))
        );
        resource.setId(null);
        return resource;
    }

    private Resource existingResource(String resourceId) {
        Resource resource = resource("orders", "0.0.2");
        resource.setId(dev.catamesh.core.model.Key.create(resourceId));
        return resource;
    }
}
