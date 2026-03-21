package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.ApplyStep;
import dev.catamesh.core.model.ApplyStepStatus;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.ApplySummary;
import dev.catamesh.core.model.ApplyStatus;

import java.time.LocalDateTime;
import java.util.List;

public class BuildApplyDataProductHandler<C> extends Handler<C> {

    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        applyDataProductContext.resolvePendingSteps();

        ApplyResult applyResult = applyDataProductContext.getApplyResult();
        ApplySummary summary = buildSummary(applyResult.getSteps());
        applyResult.setSummary(summary);
        applyResult.setStatus(resolveStatus(summary));
        applyResult.setExecutedAt(LocalDateTime.now());
    }

    private ApplySummary buildSummary(List<ApplyStep> steps) {
        int executed = 0;
        int skipped = 0;
        int failed = 0;

        for (ApplyStep step : steps) {
            if (step.getStatus() == ApplyStepStatus.EXECUTED) {
                executed++;
            } else if (step.getStatus() == ApplyStepStatus.SKIPPED) {
                skipped++;
            } else if (step.getStatus() == ApplyStepStatus.FAILED) {
                failed++;
            }
        }

        return new ApplySummary(executed, skipped, failed);
    }

    private ApplyStatus resolveStatus(ApplySummary summary) {
        if (summary.getFailed() == 0) {
            return ApplyStatus.SUCCESS;
        }
        if (summary.getExecuted() > 0 || summary.getSkipped() > 0) {
            return ApplyStatus.PARTIAL_SUCCESS;
        }
        return ApplyStatus.FAILED;
    }
}
