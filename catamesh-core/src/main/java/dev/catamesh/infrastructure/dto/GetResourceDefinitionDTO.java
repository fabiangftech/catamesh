package dev.catamesh.infrastructure.dto;

public record GetResourceDefinitionDTO(String resourceId, String version) {

    public static GetResourceDefinitionDTO create(String resourceId, String version) {
        return new GetResourceDefinitionDTO(resourceId, version);
    }
}
