package dev.catamesh.core.handler;

import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.Resource;

public class ApplyDataProductContext extends PlanDataProductContext {

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
}
