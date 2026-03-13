package dev.catamesh.infrastructure.config.v2;

import dev.catamesh.application.facade.DefaultDataProductFacade;
import dev.catamesh.application.facade.DefaultStartApplicationFacade;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.infrastructure.config.JSONConfig;
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;

public class AppConfig {
    private static final String CATAMESH = "catamesh";
    private static final String DEFAULT_H2_URL = "jdbc:h2:file:./db-file-catamesh/catamesh_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

    private final DiffConfig diffConfig;
    private final PlanConfig planConfig;
    private final JSONConfig jsonConfig;
    public AppConfig() {
        DataSource dataSource = dataSource();
        diffConfig = new DiffConfig(dataSource);
        planConfig = new PlanConfig(dataSource);
        jsonConfig = new JSONConfig();
    }

    public DataProductFacade dataProductFacade() {
        return new DefaultDataProductFacade(
                diffConfig.diffDataProductChainFactory(),
                planConfig.planDataProductChainFactory()
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
