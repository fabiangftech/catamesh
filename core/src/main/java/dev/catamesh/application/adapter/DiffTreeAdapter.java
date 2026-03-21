package dev.catamesh.application.adapter;

import dev.catamesh.core.model.DiffTreeNode;

public final class DiffTreeAdapter {
private DiffTreeAdapter(){
    // do nothing
}
    public static DiffTreeNode child(DiffTreeNode node, String name) {
        if (node == null) {
            return null;
        }

        DiffTreeNode field = node.getFields().get(name);
        return field != null ? field : node.getEntries().get(name);
    }
}
