package dev.catamesh.core.model;

import dev.catamesh.core.exception.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CoreModelCoverageTest {

    @Test
    void statusFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(Status.DRAFT, Status.fromValue("draft"));
        Assertions.assertEquals(Status.ERROR, Status.fromValue("ERROR"));
        Assertions.assertEquals("healthy", Status.HEALTHY.getValue());
        assertInvalid(() -> Status.fromValue(null));
        assertInvalid(() -> Status.fromValue("unknown"));
    }

    @Test
    void planActionFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(PlanAction.CREATE, PlanAction.fromValue("create"));
        Assertions.assertEquals(PlanAction.DELETE, PlanAction.fromValue("DELETE"));
        Assertions.assertEquals("noop", PlanAction.NOOP.getValue());
        assertInvalid(() -> PlanAction.fromValue(null));
        assertInvalid(() -> PlanAction.fromValue("invalid"));
    }

    @Test
    void diffOpFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(DiffOp.ADD, DiffOp.fromValue("add"));
        Assertions.assertEquals(DiffOp.REPLACE, DiffOp.fromValue("REPLACE"));
        Assertions.assertEquals("remove", DiffOp.REMOVE.getValue());
        assertInvalid(() -> DiffOp.fromValue(null));
        assertInvalid(() -> DiffOp.fromValue("invalid"));
    }

    @Test
    void diffScopeFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(DiffScope.DATA_PRODUCT, DiffScope.fromValue("dataProduct"));
        Assertions.assertEquals(DiffScope.RESOURCE, DiffScope.fromValue("RESOURCE"));
        Assertions.assertEquals("resource", DiffScope.RESOURCE.getValue());
        assertInvalid(() -> DiffScope.fromValue(null));
        assertInvalid(() -> DiffScope.fromValue("invalid"));
    }

    @Test
    void modelTypeFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(ModelType.DATA_PRODUCT, ModelType.fromValue("data-product"));
        Assertions.assertEquals(ModelType.ENVIRONMENT, ModelType.fromValue("ENVIRONMENT"));
        Assertions.assertEquals("deploy", ModelType.DEPLOY.getValue());
        assertInvalid(() -> ModelType.fromValue(null));
        assertInvalid(() -> ModelType.fromValue("invalid"));
    }

    @Test
    void planResourceTypeFromValueAcceptsSerializedAndEnumNames() {
        Assertions.assertEquals(PlanResourceType.RESOURCE, PlanResourceType.fromValue("resource"));
        Assertions.assertEquals(PlanResourceType.RESOURCE_DEFINITION, PlanResourceType.fromValue("RESOURCE_DEFINITION"));
        Assertions.assertEquals("resource-definition", PlanResourceType.RESOURCE_DEFINITION.getValue());
        assertInvalid(() -> PlanResourceType.fromValue(null));
        assertInvalid(() -> PlanResourceType.fromValue("invalid"));
    }

    @Test
    void planAndPlanResourcesTrackSummaryCounters() {
        Plan plan = new Plan("sales-dp");

        plan.plusCreateSummary();
        plan.plusUpdateSummary();
        plan.plusDeleteSummary();
        plan.plusNoopSummary();
        plan.setAction(PlanAction.UPDATE);
        plan.addResource(PlanResource.create("orders", PlanAction.CREATE));
        plan.addResource(PlanResource.resourceDefinition("orders", "0.0.1", PlanAction.NOOP));

        Assertions.assertNotNull(plan.getRequestId());
        Assertions.assertEquals("sales-dp", plan.getDataProductName());
        Assertions.assertEquals(PlanAction.UPDATE, plan.getAction());
        Assertions.assertEquals(1, plan.getSummary().getCreate());
        Assertions.assertEquals(1, plan.getSummary().getUpdate());
        Assertions.assertEquals(1, plan.getSummary().getDelete());
        Assertions.assertEquals(1, plan.getSummary().getNoop());
        Assertions.assertEquals(PlanResourceType.RESOURCE, plan.getResources().get(0).getType());
        Assertions.assertEquals("orders", plan.getResources().get(0).getName());
        Assertions.assertEquals(PlanAction.CREATE, plan.getResources().get(0).getAction());
        Assertions.assertEquals(PlanResourceType.RESOURCE_DEFINITION, plan.getResources().get(1).getType());
        Assertions.assertEquals("0.0.1", plan.getResources().get(1).getVersion());
        Assertions.assertEquals(PlanAction.NOOP, plan.getResources().get(1).getAction());
        Assertions.assertEquals(0, plan.getSummary().getReplace());
        Assertions.assertEquals(0, plan.getSummary().getAdopt());
    }

    private void assertInvalid(Runnable runnable) {
        Assertions.assertThrows(InvalidInputException.class, runnable::run);
    }
}
