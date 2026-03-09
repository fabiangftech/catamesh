package dev.catamesh.core.model;

public class PlanResource {
    private final PlanResourceType type;
    private final String name;
    private final String version;
    private final PlanAction action;

    public PlanResource(PlanResourceType type, String name, String version, PlanAction action) {
        this.type = type;
        this.name = name;
        this.version = version;
        this.action = action;
    }

    public PlanResourceType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public PlanAction getAction() {
        return action;
    }

    public static PlanResource create(String name, PlanAction action){
        return resource(name, action);
    }

    public static PlanResource resource(String name, PlanAction action) {
        return new PlanResource(PlanResourceType.RESOURCE, name, null, action);
    }

    public static PlanResource resourceDefinition(String name, String version, PlanAction action) {
        return new PlanResource(PlanResourceType.RESOURCE_DEFINITION, name, version, action);
    }
}
