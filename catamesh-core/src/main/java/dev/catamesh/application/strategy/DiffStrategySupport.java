package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DiffChange;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

final class DiffStrategySupport {

    private DiffStrategySupport() {
        // utility class
    }

    static String path(String basePath, String segment) {
        if (basePath == null || basePath.isBlank()) {
            return segment;
        }
        if (segment == null || segment.isBlank()) {
            return basePath;
        }
        return basePath + "." + segment;
    }

    static List<DiffChange> sortByPath(List<DiffChange> changes) {
        return changes.stream()
                .sorted(Comparator.comparing(DiffChange::getPath))
                .toList();
    }

    static void compareValue(String path,
                             Object desired,
                             boolean hasDesired,
                             Object current,
                             boolean hasCurrent,
                             List<DiffChange> changes) {
        if (!hasDesired && !hasCurrent) {
            return;
        }
        if (hasDesired && !hasCurrent) {
            changes.add(DiffChange.add(path, DiffPayloadAdapter.normalize(desired)));
            return;
        }
        if (!hasDesired) {
            changes.add(DiffChange.remove(path, DiffPayloadAdapter.normalize(current)));
            return;
        }
        if (desired instanceof Map<?, ?> desiredMap && current instanceof Map<?, ?> currentMap) {
            compareMap(path, desiredMap, currentMap, changes);
            return;
        }
        if (desired instanceof List<?> desiredList && current instanceof List<?> currentList) {
            compareList(path, desiredList, currentList, changes);
            return;
        }

        addReplaceChange(path, desired, current, changes);
    }

    private static void compareMap(String path,
                                   Map<?, ?> desiredMap,
                                   Map<?, ?> currentMap,
                                   List<DiffChange> changes) {
        Map<String, Object> desiredByKey = DiffPayloadAdapter.toStringKeyedMap(desiredMap);
        Map<String, Object> currentByKey = DiffPayloadAdapter.toStringKeyedMap(currentMap);
        SortedSet<String> keys = new TreeSet<>();
        keys.addAll(desiredByKey.keySet());
        keys.addAll(currentByKey.keySet());

        for (String key : keys) {
            boolean hasDesired = desiredByKey.containsKey(key);
            boolean hasCurrent = currentByKey.containsKey(key);
            compareValue(
                    path(path, key),
                    hasDesired ? desiredByKey.get(key) : null,
                    hasDesired,
                    hasCurrent ? currentByKey.get(key) : null,
                    hasCurrent,
                    changes
            );
        }
    }

    private static void compareList(String path,
                                    List<?> desiredList,
                                    List<?> currentList,
                                    List<DiffChange> changes) {
        Object normalizedDesired = DiffPayloadAdapter.normalize(desiredList);
        Object normalizedCurrent = DiffPayloadAdapter.normalize(currentList);
        if (!Objects.equals(normalizedDesired, normalizedCurrent)) {
            changes.add(DiffChange.replace(path, normalizedCurrent, normalizedDesired));
        }
    }

    private static void addReplaceChange(String path,
                                         Object desired,
                                         Object current,
                                         List<DiffChange> changes) {
        Object normalizedDesired = DiffPayloadAdapter.normalize(desired);
        Object normalizedCurrent = DiffPayloadAdapter.normalize(current);
        if (!Objects.equals(normalizedDesired, normalizedCurrent)) {
            changes.add(DiffChange.replace(path, normalizedCurrent, normalizedDesired));
        }
    }
}
