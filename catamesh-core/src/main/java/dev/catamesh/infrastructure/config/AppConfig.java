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
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;

public class AppConfig {
    private static final String CATAMESH = "catamesh";
    private static final String DEFAULT_H2_URL = "jdbc:h2:file:./db-file-catamesh/catamesh_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private final DiffConfig diffConfig;
    private final PlanConfig planConfig;
    private final ApplyConfig applyConfig;
    private final JSONConfig jsonConfig;

    public AppConfig() {
        DataSource dataSource = dataSource();
        StartApplicationFacade startApplicationFacade = startApplicationFacade(dataSource);
        diffConfig = new DiffConfig(dataSource);
        planConfig = new PlanConfig(dataSource);
        applyConfig = new ApplyConfig(dataSource);
        jsonConfig = new JSONConfig();
        startApplicationFacade.start();
    }

    public StartApplicationFacade startApplicationFacade(DataSource dataSource) {
        Query<String, String> getFileFromResourceQuery = new GetFileFromResourceQuery();
        Command<Void, Void> initTablesDBCommand = new InitTablesDBCommand(dataSource, getFileFromResourceQuery);
        return new DefaultStartApplicationFacade(initTablesDBCommand);
    }

    public TemplateFacade templateFacade() {
        Query<String, String> getFileFromResourceQuery = new GetFileFromResourceQuery();
        return new DefaultTemplateFacade(getFileFromResourceQuery);
    }


    public DataProductFacade dataProductFacade() {
        return new DefaultDataProductFacade(
                diffConfig.diffDataProductChainFactory(),
                planConfig.planDataProductChainFactory(),
                applyConfig.applynDataProductChainFactory()
        );
    }

    public ObjectMapper jsonMapper() {
        return this.jsonConfig.jsonMapper();
    }

    private DataSource dataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DEFAULT_H2_URL);
        dataSource.setUser(CATAMESH);
        dataSource.setPassword("");
        return dataSource;
    }
}
