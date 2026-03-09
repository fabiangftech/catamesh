package dev.catamesh.infrastructure.dto;

public record GetResourceDTO(String name, String dataProductId) {


    public static GetResourceDTO create(String name, String dataProductId) {
        return new GetResourceDTO(name, dataProductId);
    }
}
