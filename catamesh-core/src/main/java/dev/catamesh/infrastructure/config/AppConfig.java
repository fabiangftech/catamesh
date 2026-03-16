package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.DefaultDataProductFacade;
import dev.catamesh.application.facade.DefaultStartApplicationFacade;
import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.facade.StartApplicationFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.cqrs.db.AllResourcesQuery;
import dev.catamesh.infrastructure.cqrs.db.GetResourceDefinitionQuery;
import dev.catamesh.infrastructure.cqrs.db.InitTablesDBCommand;
import dev.catamesh.infrastructure.cqrs.db.OptionalDataProductQuery;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class AppConfig {
    private static final String CATAMESH = "catamesh";
    public static final String DB_DIR_SYSTEM_PROPERTY = "catamesh.db.dir";
    public static final String DB_DIR_ENV_VAR = "CATAMESH_DB_DIR";
    public static final String DEFAULT_DB_DIR = "./db-file-catamesh";
    private static final String H2_URL_SUFFIX = ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private final DiffConfig diffConfig;
    private final PlanConfig planConfig;
    private final ApplyConfig applyConfig;
    private final JSONConfig jsonConfig;
    private final CQRSConfig cqrsConfig;

    public AppConfig() {
        DataSource dataSource = dataSource();
        this.diffConfig = new DiffConfig(dataSource);
        this.planConfig = new PlanConfig(dataSource);
        this.applyConfig = new ApplyConfig(dataSource);
        this.jsonConfig = new JSONConfig();
        this.cqrsConfig= new CQRSConfig(dataSource, jsonConfig.jsonMapper());
        StartApplicationFacade startApplicationFacade = startApplicationFacade(dataSource);
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

    public ObjectMapper jsonMapper() {
        return this.jsonConfig.jsonMapper();
    }


    private DataSource dataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(resolveDatabaseUrl());
        dataSource.setUser(CATAMESH);
        dataSource.setPassword("");
        return dataSource;
    }

    static String resolveDatabaseUrl() {
        return resolveDatabaseUrl(System.getProperties(), System.getenv());
    }

    static String resolveDatabaseUrl(Properties systemProperties, Map<String, String> environment) {
        return resolveDatabaseUrl(resolveDatabaseDirectory(systemProperties, environment));
    }

    static String resolveDatabaseUrl(String databaseDirectory) {
        Path databasePath = Path.of(databaseDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve("catamesh_db");

        return "jdbc:h2:file:" + databasePath.toString().replace('\\', '/') + H2_URL_SUFFIX;
    }

    static String resolveDatabaseDirectory() {
        return resolveDatabaseDirectory(System.getProperties(), System.getenv());
    }

    static String resolveDatabaseDirectory(Properties systemProperties, Map<String, String> environment) {
        String propertyDirectory = trimToNull(systemProperties.getProperty(DB_DIR_SYSTEM_PROPERTY));
        if (propertyDirectory != null) {
            return propertyDirectory;
        }

        String environmentDirectory = trimToNull(environment.get(DB_DIR_ENV_VAR));
        if (environmentDirectory != null) {
            return environmentDirectory;
        }

        return DEFAULT_DB_DIR;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            return null;
        }

        return trimmedValue;
    }
}
