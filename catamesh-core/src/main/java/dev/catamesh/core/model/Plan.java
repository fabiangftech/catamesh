package dev.catamesh.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Plan {
    private final Key requestId;
    private final String dataProductName;
    private PlanAction action = PlanAction.NOOP;
    private PlanSummary summary;
    private final List<PlanResource> resources;

    public Plan(String dataProductName) {
        this.requestId = Key.newId();
        this.dataProductName = dataProductName;
        this.resources = new ArrayList<>();
    }

    public String getRequestId() {
        return this.requestId.value();
    }
    public String getDataProductName() {
        return dataProductName;
    }
    public PlanAction getAction() {
        return action;
    }

    public void setAction(PlanAction action) {
        this.action = action;
    }

    public PlanSummary getSummary() {
        return summary;
    }

    public void plusCreateSummary() {
        if (Objects.isNull(summary)) {
            this.summary = new PlanSummary();
        }
        this.summary.plusCreate();
    }

    public void plusUpdateSummary() {
        if (Objects.isNull(summary)) {
            this.summary = new PlanSummary();
        }
        this.summary.plusUpdate();
    }

    public void plusDeleteSummary() {
        if (Objects.isNull(summary)) {
            this.summary = new PlanSummary();
        }
        this.summary.plusDelete();
    }

    public void plusNoopSummary() {
        if (Objects.isNull(summary)) {
            this.summary = new PlanSummary();
        }
        this.summary.plusNoop();
    }

    public List<PlanResource> getResources() {
        return resources;
    }

    public void addResource(PlanResource planResource) {
        this.resources.add(planResource);
    }
}
