package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.ImmutableResourceDefinitionVersionException;
import dev.catamesh.core.handler.ApplyDataProductContext;
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

class ValidateResourceDefinitionVersionImmutabilityHandlerTest {

    @Test
    void doHandleThrowsWhenNoopDefinitionChangesContent() {
        Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> definitionQuery = dto -> Optional.of(
                new ResourceDefinition(SchemaVersion.BUCKET_V1, "0.0.1", Map.of("lifecycleDays", 30))
        );
        ValidateResourceDefinitionVersionImmutabilityHandler handler =
                new ValidateResourceDefinitionVersionImmutabilityHandler(definitionQuery);

        ApplyDataProductContext context = ApplyDataProductContext.create("yaml");
        context.setDataProduct(dataProduct(resource("orders", "0.0.1", 99)));
        context.setPlan(planWithDefinitionAction("orders", "0.0.1", PlanAction.NOOP));

        Assertions.assertThrows(ImmutableResourceDefinitionVersionException.class, () -> handler.handle(context));
    }

    private Plan planWithDefinitionAction(String resourceName, String version, PlanAction action) {
        Plan plan = new Plan("sales-dp");
        plan.addResource(PlanResource.resourceDefinition(resourceName, version, action));
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

    private Resource resource(String name, String version, int lifecycleDays) {
        Resource resource = new Resource(
                name,
                name,
                ResourceKind.BUCKET,
                new ResourceDefinition(SchemaVersion.BUCKET_V1, version, Map.of("lifecycleDays", lifecycleDays))
        );
        resource.setId(Key.create("resource-1"));
        return resource;
    }
}
