package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.*;

import java.util.ArrayList;
import java.util.List;

public final class DiffToStringAdapter {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

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
        lines.add(CYAN + "Summary: "
                  + summary.getAdded() + " added, "
                  + summary.getChanged() + " changed, "
                  + summary.getRemoved() + " removed"
                  + RESET);

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

            case CREATE ->
                    GREEN + "+ " + path + RESET +
                    " = " + stringify(node.getNewValue());

            case UPDATE ->
                    YELLOW + "~ " + path + RESET +
                    " = " + stringify(node.getOldValue()) +
                    " -> " + stringify(node.getNewValue());

            case DELETE ->
                    RED + "- " + path + RESET +
                    " = " + stringify(node.getOldValue());

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