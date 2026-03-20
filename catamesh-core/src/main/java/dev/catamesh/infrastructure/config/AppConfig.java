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
    private final DiffConfig diffConfig;
    private final PlanConfig planConfig;
    private final ApplyConfig applyConfig;
    private final CQRSConfig cqrsConfig;

    public AppConfig() {
        this.diffConfig = new DiffConfig(DataSourceConfig.dataSource());
        this.planConfig = new PlanConfig(DataSourceConfig.dataSource());
        this.applyConfig = new ApplyConfig(DataSourceConfig.dataSource());
        this.cqrsConfig= new CQRSConfig(DataSourceConfig.dataSource());
        StartApplicationFacade startApplicationFacade = startApplicationFacade(DataSourceConfig.dataSource());
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
                diffConfig.diffDataProductChainFactory(),
                planConfig.planDataProductChainFactory(),
                applyConfig.applynDataProductChainFactory(),
                this.cqrsConfig.getOptionalDataProductQuery(),
                this.cqrsConfig.getAllResourcesQuery(),
                this.cqrsConfig.getResourceDefinitionQuery()
        );
    }

    public Query<String, String> getFileFromResourceQuery() {
        return new GetFileFromResourceQuery();
    }
}
