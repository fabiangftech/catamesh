package dev.catamesh.core.exception;

import java.util.List;

public class ImmutableResourceDefinitionVersionException extends ConflictException {

    private final String resourceName;
    private final String version;
    private final List<String> changedParts;

    public ImmutableResourceDefinitionVersionException(
            String resourceName,
            String version,
            List<String> changedParts) {
        super(String.format(
                "Resource definition %s version %s is immutable. Bump definition.version to apply changes.",
                resourceName,
                version
        ));
        this.resourceName = resourceName;
        this.version = version;
        this.changedParts = List.copyOf(changedParts);
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getChangedParts() {
        return changedParts;
    }
}
