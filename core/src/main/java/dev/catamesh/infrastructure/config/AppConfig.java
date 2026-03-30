package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.DefaultDataProductFacade;
import dev.catamesh.application.facade.DefaultStartApplicationFacade;
import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.facade.StartApplicationFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;

public class AppConfig {
    public AppConfig() {
        StartApplicationFacade startApplicationFacade = startApplicationFacade();
        startApplicationFacade.start();
    }

    public StartApplicationFacade startApplicationFacade() {
        return new DefaultStartApplicationFacade(CQRSConfig.initTablesDBCommand());
    }

    public TemplateFacade templateFacade() {
        return new DefaultTemplateFacade(CQRSConfig.getFileFromResourceQuery());
    }

    public DataProductFacade dataProductFacade() {
        return new DefaultDataProductFacade(
                FactoryConfig.validateDataProductChainFactory(),
                FactoryConfig.diffDataProductChainFactory(),
                FactoryConfig.planDataProductChainFactory(),
                FactoryConfig.applyDataProductChainFactory(),
                CQRSConfig.optionalDataProductQuery(),
                CQRSConfig.allResourcesQuery(),
                CQRSConfig.getResourceDefinitionQuery()
        );
    }

    public Query<String, String> getFileFromResourceQuery() {
        return new GetFileFromResourceQuery();
    }
}
