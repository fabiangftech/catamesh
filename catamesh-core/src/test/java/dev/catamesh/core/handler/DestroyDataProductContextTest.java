package dev.catamesh.core.handler;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class DestroyDataProductContextTest {

    @Test
    void contextReturnsSafeDefaultsForMissingState() {
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan("yaml");

        Assertions.assertEquals("yaml", context.getYaml());
        Assertions.assertEquals(DestroyMode.PLAN, context.getMode());
        Assertions.assertNull(context.getName());
        Assertions.assertTrue(context.getRequestedResources().isEmpty());
        Assertions.assertTrue(context.getResources().isEmpty());
    }

    @Test
    void contextExposesRequestCurrentResourcesAndSummaryDelegation() {
        DestroyDataProductContext context = DestroyDataProductContext.createForApply("yaml");
        Plan plan = new Plan("sales-dp");

        context.setPlan(plan);
        context.setRequestDataProduct(dataProduct("sales-dp", resource("orders", "0.0.1")));
        context.setDataProduct(dataProduct("sales-dp", resource("payments", "0.0.2")));
        context.plusDeleteSummary();
        context.plusUpdateSummary();
        context.plusNoopSummary();

        Assertions.assertEquals(DestroyMode.APPLY, context.getMode());
        Assertions.assertEquals("sales-dp", context.getName());
        Assertions.assertEquals(1, context.getRequestedResources().size());
        Assertions.assertEquals("orders", context.getRequestedResources().get(0).getName());
        Assertions.assertEquals(1, context.getResources().size());
        Assertions.assertEquals("payments", context.getResources().get(0).getName());
        Assertions.assertEquals(1, plan.getSummary().getDelete());
        Assertions.assertEquals(1, plan.getSummary().getUpdate());
        Assertions.assertEquals(1, plan.getSummary().getNoop());
    }

    private DataProduct dataProduct(String name, Resource resource) {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata(name, "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource))
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
