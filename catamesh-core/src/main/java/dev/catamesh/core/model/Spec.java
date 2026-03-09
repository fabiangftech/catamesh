package dev.catamesh.core.model;

import java.util.List;

public class Spec {

    private DataProductKind kind;
    private List<Resource> resources;
    public Spec() {

    }
    public Spec(DataProductKind kind, List<Resource> resources) {
        this.kind = kind;
        this.resources = resources;
    }

    public DataProductKind getKind() {
        return kind;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
