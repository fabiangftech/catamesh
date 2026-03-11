package dev.catamesh.application.strategy;

import dev.catamesh.core.model.v2.DiffChangeType;
import dev.catamesh.core.model.v2.DiffNodeKind;
import dev.catamesh.core.model.v2.DiffSupport;
import dev.catamesh.core.model.v2.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DiffObjectStrategy implements DiffStrategy<DiffTreeNode> {

    private final Set<String> ignoredPaths;

    public DiffObjectStrategy(Set<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        Map<String, DiffTreeNode> fields = new LinkedHashMap<>();

        for (Field field : DiffSupport.getFields(desired.getClass())) {

            Object dv = DiffSupport.read(field, desired);
            Object cv = DiffSupport.read(field, current);

            String child = DiffSupport.childPath(path, field.getName());
            String childNormalized = DiffSupport.normalizeListPath(child);
            if (ignoredPaths.contains(childNormalized)) {
                continue;
            }
            fields.put(field.getName(), diffNode(child, dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.OBJECT, DiffChangeType.NONE, current, desired, fields, null, null);
    }
}
