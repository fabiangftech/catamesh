package dev.catamesh.application.strategy;

import dev.catamesh.core.model.v2.DiffChangeType;
import dev.catamesh.core.model.v2.DiffNodeKind;
import dev.catamesh.core.model.v2.DiffSupport;
import dev.catamesh.core.model.v2.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DiffMapStrategy implements DiffStrategy<DiffTreeNode> {
    private final DiffStrategy<DiffTreeNode> diffStrategy;

    public DiffMapStrategy(DiffStrategy<DiffTreeNode> diffStrategy) {
        this.diffStrategy = diffStrategy;
    }

    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        Map<?, ?> dm = desired instanceof Map<?, ?> map ? map : Collections.emptyMap();
        Map<?, ?> cm = current instanceof Map<?, ?> map ? map : Collections.emptyMap();

        Map<String, DiffTreeNode> entries = new LinkedHashMap<>();

        Set<Object> keys = new LinkedHashSet<>();
        keys.addAll(dm.keySet());
        keys.addAll(cm.keySet());

        for (Object key : keys) {

            Object dv = dm.get(key);
            Object cv = cm.get(key);

            String child = DiffSupport.childPath(path, key.toString());

            entries.put(key.toString(), diffStrategy.diffNode(child, dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.MAP, DiffChangeType.NONE, current, desired, null, null, entries);

    }
}
