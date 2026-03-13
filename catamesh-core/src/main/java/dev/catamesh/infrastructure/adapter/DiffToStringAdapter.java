package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DiffChangeType;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.DiffSummary;
import dev.catamesh.core.model.DiffTreeNode;

import java.util.ArrayList;
import java.util.List;

public final class DiffToStringAdapter {
    public static String toString(DiffResult diffResult) {
        if (diffResult == null) {
            return "Diff: null";
        }

        List<String> lines = new ArrayList<>();
        appendNode(diffResult.getRoot(), lines);

        if (lines.isEmpty()) {
            lines.add("No changes");
        }

        DiffSummary summary = diffResult.getSummary();
        lines.add("");
        lines.add("Summary: " +
                  summary.getAdded() + " added, " +
                  summary.getAdded() + " changed, " +
                  summary.getRemoved() + " removed");

        return String.join(System.lineSeparator(), lines);
    }

    private static void appendNode(DiffTreeNode node, List<String> lines) {
        if (node == null) {
            return;
        }

        if (node.getChangeType() != DiffChangeType.NONE) {
            lines.add(formatLine(node));
        }

        for (DiffTreeNode child : node.getFields().values()) {
            appendNode(child, lines);
        }

        for (DiffTreeNode child : node.getElements()) {
            appendNode(child, lines);
        }

        for (DiffTreeNode child : node.getEntries().values()) {
            appendNode(child, lines);
        }
    }

    private static String formatLine(DiffTreeNode node) {
        String path = normalize(node.getPath());

        return switch (node.getChangeType()) {
            case CREATE -> "+ " + path + " = " + stringify(node.getNewValue());
            case UPDATE -> "~ " + path + " = " + stringify(node.getOldValue()) + " -> " + stringify(node.getNewValue());
            case DELETE -> "- " + path + " = " + stringify(node.getOldValue());
            case NONE -> path;
        };
    }

    private static String normalize(String path) {
        if (path == null || path.isBlank()) {
            return "<root>";
        }
        return path;
    }

    private static String stringify(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        return String.valueOf(value);
    }
}
