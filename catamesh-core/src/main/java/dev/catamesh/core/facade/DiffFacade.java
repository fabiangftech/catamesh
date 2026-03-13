package dev.catamesh.core.facade;

import dev.catamesh.core.model.DiffResult;

public interface DiffFacade {
    DiffResult compare(Object desired, Object current);
}
