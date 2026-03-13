package dev.catamesh.core.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DiffTreeNode {

    private final String path;
    private final DiffNodeKind kind;
    private final DiffChangeType changeType;

    private final Object oldValue;
    private final Object newValue;

    private final Map<String, DiffTreeNode> fields;
    private final List<DiffTreeNode> elements;
    private final Map<String, DiffTreeNode> entries;

    public DiffTreeNode(
            String path,
            DiffNodeKind kind,
            DiffChangeType changeType,
            Object oldValue,
            Object newValue,
            Map<String, DiffTreeNode> fields,
            List<DiffTreeNode> elements,
            Map<String, DiffTreeNode> entries
    ) {
        this.path = path;
        this.kind = kind;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;

        this.fields = fields == null ? Map.of() : Map.copyOf(fields);
        this.elements = elements == null ? List.of() : List.copyOf(elements);
        this.entries = entries == null ? Map.of() : Map.copyOf(entries);
    }

    public DiffSummary computeSummary() {

        DiffSummary summary = new DiffSummary();

        accumulate(summary);

        return summary;
    }

    private void accumulate(DiffSummary summary) {

        switch (changeType) {
            case CREATE -> summary.addAdded();
            case UPDATE -> summary.addChanged();
            case DELETE -> summary.addRemoved();
            default -> {}
        }

        for (DiffTreeNode child : fields.values()) {
            child.accumulate(summary);
        }

        for (DiffTreeNode child : elements) {
            child.accumulate(summary);
        }

        for (DiffTreeNode child : entries.values()) {
            child.accumulate(summary);
        }
    }

    public String getPath() {
        return path;
    }

    public DiffNodeKind getKind() {
        return kind;
    }

    public DiffChangeType getChangeType() {
        return changeType;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Map<String, DiffTreeNode> getFields() {
        return fields;
    }

    public List<DiffTreeNode> getElements() {
        return elements;
    }

    public Map<String, DiffTreeNode> getEntries() {
        return entries;
    }


    public static boolean hasChanges(DiffTreeNode node) {
        if (node == null) {
            return false;
        }

        if (node.getChangeType() != DiffChangeType.NONE) {
            return true;
        }

        for (DiffTreeNode child : node.getFields().values()) {
            if (hasChanges(child)) {
                return true;
            }
        }

        for (DiffTreeNode child : node.getEntries().values()) {
            if (hasChanges(child)) {
                return true;
            }
        }

        for (DiffTreeNode child : node.getElements()) {
            if (hasChanges(child)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasChangesExcluding(DiffTreeNode node, Set<String> excludedChildren) {
        if (node == null) {
            return false;
        }

        if (node.getChangeType() != DiffChangeType.NONE) {
            return true;
        }

        for (Map.Entry<String, DiffTreeNode> field : node.getFields().entrySet()) {
            if (!excludedChildren.contains(field.getKey()) && DiffTreeNode.hasChanges(field.getValue())) {
                return true;
            }
        }

        for (Map.Entry<String, DiffTreeNode> entry : node.getEntries().entrySet()) {
            if (!excludedChildren.contains(entry.getKey()) && DiffTreeNode.hasChanges(entry.getValue())) {
                return true;
            }
        }

        for (DiffTreeNode child : node.getElements()) {
            if (DiffTreeNode.hasChanges(child)) {
                return true;
            }
        }

        return false;
    }
}
