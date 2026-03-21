package dev.catamesh.core.cqrs;

public interface Command<I, O> {
    O execute(I input);

    default O execute() {
        return execute(null);
    }
}
