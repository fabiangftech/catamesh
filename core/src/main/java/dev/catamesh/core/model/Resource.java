package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resource {
    private Key id;
    private Key dataProductId;
    private String name;
    private String displayName;
    private ResourceKind kind;
    private ResourceDefinition definition;

    public Resource() {
    }

    public Resource(Key id, Key dataProductId, String name, String displayName, ResourceKind kind) {
        this.id = id;
        this.dataProductId = dataProductId;
        this.name = name;
        this.displayName = displayName;
        this.kind = kind;
    }

    public Resource(String name, String displayName, ResourceKind kind, ResourceDefinition definition) {
        this.id = Key.newId();
        this.name = name;
        this.displayName = displayName;
        this.kind = kind;
        this.definition = definition;
    }

    @JsonIgnore
    public Key getKey() {
        return this.id;
    }

    public String getId() {
        return Objects.isNull(id) ? null : id.value();
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getDataProductId() {
        return Objects.isNull(dataProductId) ? null : dataProductId.value();
    }

    public void setDataProductId(Key dataProductId) {
        this.dataProductId = dataProductId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return Objects.isNull(displayName) ? name : displayName;
    }

    public ResourceKind getKind() {
        return kind;
    }

    public ResourceDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ResourceDefinition definition) {
        this.definition = definition;
    }

    @JsonIgnore
    public String getResourcePath() {
        return "spec.resources." + name;
    }
}
