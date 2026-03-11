package dev.catamesh.application.strategy;

import dev.catamesh.core.model.v2.DiffChangeType;
import dev.catamesh.core.model.v2.DiffNodeKind;
import dev.catamesh.core.model.v2.DiffSupport;
import dev.catamesh.core.model.v2.DiffTreeNode;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.*;

public class DiffListStrategy implements DiffStrategy<DiffTreeNode> {
    private final DiffStrategy<DiffTreeNode> diffStrategy;

    public DiffListStrategy(DiffStrategy<DiffTreeNode> diffStrategy) {
        this.diffStrategy = diffStrategy;
    }

    @Override
    public DiffTreeNode diffNode(String path, Object desired, Object current) {
        List<?> dl = DiffSupport.toList(desired);
        List<?> cl = DiffSupport.toList(current);

        List<DiffTreeNode> elements = new ArrayList<>();

        int max = Math.max(dl.size(), cl.size());

        for (int i = 0; i < max; i++) {

            Object dv = i < dl.size() ? dl.get(i) : null;
            Object cv = i < cl.size() ? cl.get(i) : null;

            elements.add(diffStrategy.diffNode(path + "[" + i + "]", dv, cv));
        }

        return new DiffTreeNode(path, DiffNodeKind.LIST, DiffChangeType.NONE, current, desired, null, elements, null);
    }
}
