package dev.catamesh.application.handler;

import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class PlanDestroyTerminalHandler extends Handler<DestroyDataProductContext> {

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        // Intentionally empty: this handler acts as a terminal node for the plan pipeline.
    }
}
