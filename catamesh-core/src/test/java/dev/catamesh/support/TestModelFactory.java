package dev.catamesh.support;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;

import java.util.List;
import java.util.Map;

public final class TestModelFactory {

    private TestModelFactory() {
    }

    public static DataProduct dataProduct(String name, List<Resource> resources) {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata(Key.create("dp-" + name), name, "Display " + name, "domain", "description"),
                new Spec(DataProductKind.SOURCE_ALIGNED, resources)
        );
    }

    public static Resource bucketResource(String name, String version) {
        return resource(name, ResourceKind.BUCKET, bucketDefinition(version, 30));
    }

    public static Resource resource(String name, ResourceKind kind, ResourceDefinition definition) {
        Resource resource = new Resource(name, "Display " + name, kind, definition);
        resource.setId(Key.create("resource-" + name));
        resource.setDataProductId(Key.create("dp-" + name));
        return resource;
    }

    public static ResourceDefinition bucketDefinition(String version, int lifecycleDays) {
        return new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                version,
                Map.of("lifecycleDays", lifecycleDays)
        );
    }
}
