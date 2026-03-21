package dev.catamesh.core.handler;

import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.model.ApplyStep;
import dev.catamesh.core.model.ApplyStepStatus;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.Resource;

import java.util.List;
import java.util.Objects;

public class ApplyDataProductContext extends PlanDataProductContext {
    private static final String NO_CHANGES_MESSAGE = "No changes to apply";
    private static final String UNSUPPORTED_ACTION_MESSAGE = "Action not supported by current apply pipeline";
    private static final String CREATE_NOT_EXECUTED_MESSAGE = "Create action was not executed by current apply pipeline";

    private ApplyResult applyResult;
    protected ApplyDataProductContext(String yaml) {
        super(yaml);
    }

    public ApplyResult getApplyResult() {
        return applyResult;
    }

    public void setApplyResult(ApplyResult applyResult) {
        this.applyResult = applyResult;
    }

    public List<ApplyStep> getApplySteps() {
        return requireApplyResult().getSteps();
    }

    public void markStepExecuted(String path) {
        markStep(path, ApplyStepStatus.EXECUTED, null);
    }

    public void markStepSkipped(String path, String message) {
        markStep(path, ApplyStepStatus.SKIPPED, message);
    }

    public void resolvePendingSteps() {
        getApplySteps().stream()
                .filter(step -> Objects.isNull(step.getStatus()))
                .forEach(this::resolvePendingStep);
    }

    public static ApplyDataProductContext create(String yaml){
        return new ApplyDataProductContext(yaml);
    }

    public static Resource resolve(ApplyDataProductContext context, String path) {
        return context.getDesiredResources()
                .stream()
                .filter(resource -> resource.getResourcePath().equals(path))
                .findFirst()
                .orElseThrow(() -> new InvariantException(String.format("Resource path=%s was not found in desired data product", path)));
    }

    public static String resolveDataProductId(ApplyDataProductContext context, Resource resource) {
        String desiredDataProductId = context.getDesiredDataProduct().getMetadata().getId();
        if (desiredDataProductId != null) {
            return desiredDataProductId;
        }

        if (context.getCurrentDataProduct() != null && context.getCurrentDataProduct().getMetadata() != null) {
            String currentDataProductId = context.getCurrentDataProduct().getMetadata().getId();
            if (currentDataProductId != null) {
                return currentDataProductId;
            }
        }

        throw new InvariantException(String.format("Data product id is required to save resource=%s", resource.getName()));
    }

    private ApplyResult requireApplyResult() {
        if (applyResult == null) {
            throw new InvariantException("Apply result is not initialized");
        }
        return applyResult;
    }

    private void resolvePendingStep(ApplyStep step) {
        switch (step.getAction()) {
            case NOOP -> step.setMessage(NO_CHANGES_MESSAGE);
            case UPDATE, DELETE, REPLACE -> step.setMessage(UNSUPPORTED_ACTION_MESSAGE);
            case CREATE -> step.setMessage(CREATE_NOT_EXECUTED_MESSAGE);
        }
        step.setStatus(ApplyStepStatus.SKIPPED);
    }

    private void markStep(String path, ApplyStepStatus status, String message) {
        ApplyStep step = getApplySteps()
                .stream()
                .filter(candidate -> candidate.getPath().equals(path))
                .findFirst()
                .orElseThrow(() -> new InvariantException(String.format("Apply step path=%s was not found", path)));
        step.setStatus(status);
        step.setMessage(message);
    }
}
