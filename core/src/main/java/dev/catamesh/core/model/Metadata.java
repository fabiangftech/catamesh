package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {

    private Key id;
    private String name;
    private String displayName;
    private String domain;
    private String description;

    public Metadata() {
    }

    public Metadata(Key id, String name, String displayName, String domain, String description) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.domain = domain;
        this.description = description;
    }

    public Metadata(String name, String displayName, String domain, String description) {
        this.name = name;
        this.displayName = displayName;
        this.domain = domain;
        this.description = description;
    }

    public String getId() {
        return Objects.isNull(id) ? null : id.value();
    }

    public void setId(Key id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = Key.create(id);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return Objects.isNull(displayName)?name:displayName;
    }

    public String getDomain() {
        return domain;
    }

    public String getDescription() {
        return description;
    }
}
