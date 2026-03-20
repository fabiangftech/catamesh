package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.PlanEngineFacade;
import dev.catamesh.application.strategy.PlanMetadataStrategy;
import dev.catamesh.application.strategy.PlanResourcesStrategy;
import dev.catamesh.application.strategy.PlanSpecStrategy;
import dev.catamesh.core.strategy.PlanStrategy;

public final class PlanStrategyConfig {

    private PlanStrategyConfig(){
        // do nothing
    }


    public static PlanStrategy planMetadataStrategy() {
        return new PlanMetadataStrategy();
    }

    public static PlanStrategy planResourcesStrategy() {
        return new PlanResourcesStrategy();
    }

    public static PlanStrategy planSpecStrategy() {
        return new PlanSpecStrategy(planResourcesStrategy());
    }

    public static PlanEngineFacade planEngineFacade() {
        return new PlanEngineFacade(planMetadataStrategy(), planSpecStrategy());
    }
}
