package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataProduct {
    private SchemaVersion schemaVersion;
    private Metadata metadata;
    private Spec spec;

    public DataProduct() {
        // do nothing
    }

    public DataProduct(SchemaVersion schemaVersion, Metadata metadata, Spec spec) {
        this.schemaVersion = schemaVersion;
        this.metadata = metadata;
        this.spec = spec;
    }

    public SchemaVersion getSchemaVersion() {
        return schemaVersion;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Spec getSpec() {
        return spec;
    }

    public void setResources(List<Resource> resources) {
        this.spec.setResources(resources);
    }


    public static boolean isSame(DataProduct current, DataProduct candidate) {
        var cm = current.getMetadata();
        var nm = candidate.getMetadata();
        var cs = current.getSpec();
        var ns = candidate.getSpec();
        return Objects.equals(current.getSchemaVersion(), candidate.getSchemaVersion())
               && Objects.equals(cm.getName(), nm.getName())
               && Objects.equals(cm.getDescription(), nm.getDescription())
               && Objects.equals(cm.getDisplayName(), nm.getDisplayName())
               && Objects.equals(cm.getDomain(), nm.getDomain())
               && Objects.equals(cs.getKind(), ns.getKind());
    }
}
