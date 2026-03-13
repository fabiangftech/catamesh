package dev.catamesh.application.facade;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;

public class DefaultDataProductFacade implements DataProductFacade {
    private final Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory;
    private final Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory;
    private final Factory<Void, Handler<ApplyDataProductContext>> applyDataProductChainFactory;

    public DefaultDataProductFacade(Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory,
                                    Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory,
                                    Factory<Void, Handler<ApplyDataProductContext>> applyDataProductChainFactory) {
        this.diffDataProductChainFactory = diffDataProductChainFactory;
        this.planDataProductChainFactory = planDataProductChainFactory;
        this.applyDataProductChainFactory=applyDataProductChainFactory;
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

    @Override
    public ApplyResult apply(String yaml) {
        Handler<ApplyDataProductContext> chain = applyDataProductChainFactory.create();
        ApplyDataProductContext context = ApplyDataProductContext.create(yaml);
        chain.handle(context);
        return context.getApplyResult();
    }
}
