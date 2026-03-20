package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.DefaultDataProductFacade;
import dev.catamesh.application.facade.DefaultStartApplicationFacade;
import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.facade.StartApplicationFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.cqrs.db.InitTablesDBCommand;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;

import javax.sql.DataSource;

public class AppConfig {
    private final ApplyConfig applyConfig;
    private final CQRSConfig cqrsConfig;

    public AppConfig() {
        this.applyConfig = new ApplyConfig();
        this.cqrsConfig= new CQRSConfig(DataSourceConfig.get());
        StartApplicationFacade startApplicationFacade = startApplicationFacade(DataSourceConfig.get());
        startApplicationFacade.start();
    }

    public StartApplicationFacade startApplicationFacade(DataSource dataSource) {
        Command<Void, Void> initTablesDBCommand = new InitTablesDBCommand(dataSource, this.cqrsConfig.getFileFromResourceQuery());
        return new DefaultStartApplicationFacade(initTablesDBCommand);
    }

    public TemplateFacade templateFacade() {
        return new DefaultTemplateFacade(this.cqrsConfig.getFileFromResourceQuery());
    }


    public DataProductFacade dataProductFacade() {
        return new DefaultDataProductFacade(
                DiffConfig.diffDataProductChainFactory(),
                PlanConfig.planDataProductChainFactory(),
                applyConfig.applynDataProductChainFactory(),
                CQRSConfig.optionalDataProductQuery(),
                CQRSConfig.allResourcesQuery(),
                CQRSConfig.getResourceDefinitionQuery()
        );
    }

    public Query<String, String> getFileFromResourceQuery() {
        return new GetFileFromResourceQuery();
    }
}
