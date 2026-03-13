package dev.catamesh.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.DiffDataProductContext;
import dev.catamesh.core.handler.v2.PlanDataProductContext;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;

public class DefaultDataProductFacade implements DataProductFacade {
    private final Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory;
    private final Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory;

    public DefaultDataProductFacade(Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory,
                                    Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory) {
        this.diffDataProductChainFactory = diffDataProductChainFactory;
        this.planDataProductChainFactory = planDataProductChainFactory;
    }

    @Override
    public DiffResult diff(String yaml) {
        Handler<DiffDataProductContext> chain = diffDataProductChainFactory.create();
        DiffDataProductContext context = DiffDataProductContext.create(yaml);
        chain.handle(context);
        return context.getDiffResult();
    }

    @Override
    public PlanResult plan(String yaml) {
        Handler<PlanDataProductContext> chain = planDataProductChainFactory.create();
        PlanDataProductContext context = PlanDataProductContext.create(yaml);
        chain.handle(context);
        return context.getPlanResult();
    }
}
