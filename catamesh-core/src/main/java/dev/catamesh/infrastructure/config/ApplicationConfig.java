package dev.catamesh.infrastructure.config;

import com.networknt.schema.Schema;
import dev.catamesh.application.facade.DefaultDataProductFacade;
import dev.catamesh.application.facade.DefaultStartApplicationFacade;
import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.application.factory.ApplyDataProductPipelineFactory;
import dev.catamesh.application.factory.ApplyDestroyDataProductPipelineFactory;
import dev.catamesh.application.factory.DiffDataProductPipelineFactory;
import dev.catamesh.application.factory.PlanDataProductPipelineFactory;
import dev.catamesh.application.factory.PlanDestroyDataProductPipelineFactory;
import dev.catamesh.application.handler.BuildDataProductDiffSectionHandler;
import dev.catamesh.application.handler.BuildDiffResultHandler;
import dev.catamesh.application.handler.BuildDiffSummaryHandler;
import dev.catamesh.application.handler.BuildResourceDiffSectionsHandler;
import dev.catamesh.application.handler.CheckIfExistDataProductHandler;
import dev.catamesh.application.handler.CheckIfExistResourceDefinitionVersionHandler;
import dev.catamesh.application.handler.CheckIfExistResourcesHandler;
import dev.catamesh.application.handler.CreateDataProductHandler;
import dev.catamesh.application.handler.CreateResourceDefinitionsHandler;
import dev.catamesh.application.handler.CreateResourcesHandler;
import dev.catamesh.application.handler.DestroyDataProductHandler;
import dev.catamesh.application.handler.DestroyResourceDefinitionHandler;
import dev.catamesh.application.handler.DestroyResourceHandler;
import dev.catamesh.application.handler.DiffComparisonSupport;
import dev.catamesh.application.handler.GetOptionalDataProductForDestroyHandler;
import dev.catamesh.application.handler.GetResourcesForDestroyHandler;
import dev.catamesh.application.handler.LoadCurrentDataProductForDiffHandler;
import dev.catamesh.application.handler.PlanCheckResourceDefinitionVersionHandler;
import dev.catamesh.application.handler.PlanDestroyDataProductHandler;
import dev.catamesh.application.handler.PlanDestroyTerminalHandler;
import dev.catamesh.application.handler.ValidateBucketDefinitionSchemaHandler;
import dev.catamesh.application.handler.ValidateDataProductSchemaHandler;
import dev.catamesh.application.handler.ValidateDestroyBucketDefinitionSchemaHandler;
import dev.catamesh.application.handler.ValidateDestroyDataProductSchemaHandler;
import dev.catamesh.application.handler.ValidateDestroyDefinitionVersionHandler;
import dev.catamesh.application.handler.ValidateDestroyResourceSchemaHandler;
import dev.catamesh.application.handler.ValidateResourceSchemaHandler;
import dev.catamesh.application.handler.YAMLToDataProductHandler;
import dev.catamesh.application.handler.YAMLToDestroyDataProductHandler;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.facade.StartApplicationFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.cqrs.db.AllResourcesQuery;
import dev.catamesh.infrastructure.cqrs.db.CountResourceDefinitionsByResourceIdQuery;
import dev.catamesh.infrastructure.cqrs.db.CreateDataProductCommand;
import dev.catamesh.infrastructure.cqrs.db.CreateResourceCommand;
import dev.catamesh.infrastructure.cqrs.db.CreateResourceDefinitionCommand;
import dev.catamesh.infrastructure.cqrs.db.DeactivateResourceDefinitionsByResourceIdCommand;
import dev.catamesh.infrastructure.cqrs.db.DeleteDataProductCommand;
import dev.catamesh.infrastructure.cqrs.db.DeleteResourceCommand;
import dev.catamesh.infrastructure.cqrs.db.DeleteResourceDefinitionCommand;
import dev.catamesh.infrastructure.cqrs.db.GetResourceDefinitionQuery;
import dev.catamesh.infrastructure.cqrs.db.InitTablesDBCommand;
import dev.catamesh.infrastructure.cqrs.db.OptionalDataProductQuery;
import dev.catamesh.infrastructure.cqrs.db.OptionalResourceDefinitionQuery;
import dev.catamesh.infrastructure.cqrs.db.OptionalResourceQuery;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

public class ApplicationConfig {

    private static final String CATAMESH = "catamesh";
    private DataSource dataSource;
    private StartApplicationFacade startApplicationFacade;
    private DataProductFacade dataProductFacade;
    private TemplateFacade templateFacade;

    public ApplicationConfig() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL("jdbc:h2:file:./db-file-catamesh/catamesh_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        jdbcDataSource.setUser(CATAMESH);
        jdbcDataSource.setPassword(CATAMESH);
        init(jdbcDataSource);
    }

    public ApplicationConfig(DataSource dataSource) {
        init(dataSource);
    }

    public void init(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource is required");
        JSONConfig jsonConfig = new JSONConfig();
        YAMLConfig yamlConfig = new YAMLConfig();
        ObjectMapper jsonMapper = jsonConfig.jsonMapper();
        ObjectMapper yamlMapper = yamlConfig.yamlMapper();
        Schema dataProductSchema = jsonConfig.dataProductSchema();
        Schema resourceSchema = jsonConfig.resourceSchema();
        Schema bucketSchema = jsonConfig.bucketSchema();

        GetFileFromResourceQuery getFileFromResourceQuery = new GetFileFromResourceQuery();

        CreateDataProductCommand createDataProductCommand = new CreateDataProductCommand(this.dataSource);
        CreateResourceCommand createResourceCommand = new CreateResourceCommand(this.dataSource);
        CreateResourceDefinitionCommand createResourceDefinitionCommand =
                new CreateResourceDefinitionCommand(this.dataSource, jsonMapper);
        DeactivateResourceDefinitionsByResourceIdCommand deactivateResourceDefinitionsByResourceIdCommand =
                new DeactivateResourceDefinitionsByResourceIdCommand(this.dataSource);
        DeleteDataProductCommand deleteDataProductCommand = new DeleteDataProductCommand(this.dataSource);
        DeleteResourceCommand deleteResourceCommand = new DeleteResourceCommand(this.dataSource);
        DeleteResourceDefinitionCommand deleteResourceDefinitionCommand =
                new DeleteResourceDefinitionCommand(this.dataSource);
        OptionalDataProductQuery optionalDataProductQuery = new OptionalDataProductQuery(this.dataSource);
        OptionalResourceQuery optionalResourceQuery = new OptionalResourceQuery(this.dataSource);
        OptionalResourceDefinitionQuery optionalResourceDefinitionQuery =
                new OptionalResourceDefinitionQuery(this.dataSource);
        AllResourcesQuery allResourcesQuery = new AllResourcesQuery(this.dataSource);
        CountResourceDefinitionsByResourceIdQuery countResourceDefinitionsByResourceIdQuery =
                new CountResourceDefinitionsByResourceIdQuery(this.dataSource);
        GetResourceDefinitionQuery getResourceDefinitionQuery =
                new GetResourceDefinitionQuery(this.dataSource, jsonMapper);
        InitTablesDBCommand initTablesDBCommand =
                new InitTablesDBCommand(this.dataSource, getFileFromResourceQuery);

        ApplyDataProductPipelineFactory applyDataProductPipelineFactory = new ApplyDataProductPipelineFactory(
                new YAMLToDataProductHandler(yamlMapper),
                new ValidateDataProductSchemaHandler(dataProductSchema, jsonMapper),
                new ValidateResourceSchemaHandler(resourceSchema, jsonMapper),
                new ValidateBucketDefinitionSchemaHandler(bucketSchema, jsonMapper),
                new CheckIfExistDataProductHandler(optionalDataProductQuery),
                new CreateDataProductHandler(createDataProductCommand),
                new CheckIfExistResourcesHandler(optionalResourceQuery),
                new CreateResourcesHandler(createResourceCommand),
                new CheckIfExistResourceDefinitionVersionHandler(optionalResourceDefinitionQuery, optionalResourceQuery),
                new CreateResourceDefinitionsHandler(
                        deactivateResourceDefinitionsByResourceIdCommand,
                        createResourceDefinitionCommand
                )
        );

        PlanDataProductPipelineFactory planDataProductPipelineFactory = new PlanDataProductPipelineFactory(
                new YAMLToDataProductHandler(yamlMapper),
                new ValidateDataProductSchemaHandler(dataProductSchema, jsonMapper),
                new ValidateResourceSchemaHandler(resourceSchema, jsonMapper),
                new ValidateBucketDefinitionSchemaHandler(bucketSchema, jsonMapper),
                new CheckIfExistDataProductHandler(optionalDataProductQuery),
                new CheckIfExistResourcesHandler(optionalResourceQuery),
                new PlanCheckResourceDefinitionVersionHandler(optionalResourceDefinitionQuery, optionalResourceQuery)
        );

        DiffComparisonSupport diffComparisonSupport = new DiffComparisonSupport();
        DiffDataProductPipelineFactory diffDataProductPipelineFactory = new DiffDataProductPipelineFactory(
                new YAMLToDataProductHandler(yamlMapper),
                new ValidateDataProductSchemaHandler(dataProductSchema, jsonMapper),
                new ValidateResourceSchemaHandler(resourceSchema, jsonMapper),
                new ValidateBucketDefinitionSchemaHandler(bucketSchema, jsonMapper),
                new CheckIfExistDataProductHandler(optionalDataProductQuery),
                new CheckIfExistResourcesHandler(optionalResourceQuery),
                new PlanCheckResourceDefinitionVersionHandler(optionalResourceDefinitionQuery, optionalResourceQuery),
                new LoadCurrentDataProductForDiffHandler(
                        optionalDataProductQuery,
                        allResourcesQuery,
                        getResourceDefinitionQuery
                ),
                new BuildDataProductDiffSectionHandler(diffComparisonSupport),
                new BuildResourceDiffSectionsHandler(diffComparisonSupport),
                new BuildDiffSummaryHandler(),
                new BuildDiffResultHandler()
        );

        PlanDestroyDataProductPipelineFactory planDestroyDataProductPipelineFactory =
                new PlanDestroyDataProductPipelineFactory(
                        new YAMLToDestroyDataProductHandler(yamlMapper),
                        new ValidateDestroyDataProductSchemaHandler(dataProductSchema, jsonMapper),
                        new ValidateDestroyResourceSchemaHandler(resourceSchema, jsonMapper),
                        new ValidateDestroyBucketDefinitionSchemaHandler(bucketSchema, jsonMapper),
                        new ValidateDestroyDefinitionVersionHandler(),
                        new GetOptionalDataProductForDestroyHandler(optionalDataProductQuery),
                        new GetResourcesForDestroyHandler(allResourcesQuery),
                        new PlanDestroyDataProductHandler(
                                optionalResourceDefinitionQuery,
                                countResourceDefinitionsByResourceIdQuery
                        ),
                        new PlanDestroyTerminalHandler()
                );

        ApplyDestroyDataProductPipelineFactory applyDestroyDataProductPipelineFactory =
                new ApplyDestroyDataProductPipelineFactory(
                        new YAMLToDestroyDataProductHandler(yamlMapper),
                        new ValidateDestroyDataProductSchemaHandler(dataProductSchema, jsonMapper),
                        new ValidateDestroyResourceSchemaHandler(resourceSchema, jsonMapper),
                        new ValidateDestroyBucketDefinitionSchemaHandler(bucketSchema, jsonMapper),
                        new ValidateDestroyDefinitionVersionHandler(),
                        new GetOptionalDataProductForDestroyHandler(optionalDataProductQuery),
                        new GetResourcesForDestroyHandler(allResourcesQuery),
                        new PlanDestroyDataProductHandler(
                                optionalResourceDefinitionQuery,
                                countResourceDefinitionsByResourceIdQuery
                        ),
                        new DestroyDataProductHandler(deleteDataProductCommand, allResourcesQuery),
                        new DestroyResourceHandler(deleteResourceCommand),
                        new DestroyResourceDefinitionHandler(deleteResourceDefinitionCommand)
                );

        this.startApplicationFacade = new DefaultStartApplicationFacade(initTablesDBCommand);
        this.templateFacade = new DefaultTemplateFacade(getFileFromResourceQuery);
        this.dataProductFacade = new DefaultDataProductFacade(
                applyDataProductPipelineFactory,
                planDataProductPipelineFactory,
                diffDataProductPipelineFactory,
                planDestroyDataProductPipelineFactory,
                applyDestroyDataProductPipelineFactory,
                optionalDataProductQuery,
                allResourcesQuery,
                getResourceDefinitionQuery
        );
        this.startApplicationFacade.start();
    }

    public StartApplicationFacade startApplicationFacade() {
        return startApplicationFacade;
    }

    public DataProductFacade dataProductFacade() {
        return dataProductFacade;
    }

    public TemplateFacade templateFacade() {
        return templateFacade;
    }

    private static final class DriverManagerDataSource implements DataSource {
        private final String url;
        private final String user;
        private final String password;

        private DriverManagerDataSource(String url, String user, String password) {
            this.url = Objects.requireNonNull(url, "url is required");
            this.user = user;
            this.password = password;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }

        @Override
        public Connection getConnection(String username, String pwd) throws SQLException {
            return DriverManager.getConnection(url, username, pwd);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return DriverManager.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            DriverManager.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            DriverManager.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return DriverManager.getLoginTimeout();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isInstance(this)) {
                return iface.cast(this);
            }
            throw new SQLException("No wrapper for " + iface.getName());
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return iface.isInstance(this);
        }
    }
}
