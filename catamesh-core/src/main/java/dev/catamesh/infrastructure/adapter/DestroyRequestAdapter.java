package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.Resource;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class DestroyRequestAdapter {

    private DestroyRequestAdapter() {
        // utility class
    }

    public static Map<String, LinkedHashSet<String>> requestedDefinitionVersionsByResource(List<Resource> requestedResources) {
        Map<String, LinkedHashSet<String>> requestedDefinitionVersionsByResource = new LinkedHashMap<>();
        requestedResources.forEach(resource -> requestedDefinitionVersionsByResource
                .computeIfAbsent(resource.getName(), key -> new LinkedHashSet<>())
                .add(resource.getDefinition().getVersion()));
        return requestedDefinitionVersionsByResource;
    }
}
