package dev.catamesh.core.handler;

import java.util.Objects;

public abstract class Handler<I> {
    private Handler<I> next;

    public Handler<I> link(Handler<I> next) {
        this.next = next;
        return next;
    }

    public final void handle(I context) {
        doHandle(context);
        if (Objects.nonNull(next)) {
            next.handle(context);
        }
    }

    protected abstract void doHandle(I context);
}
