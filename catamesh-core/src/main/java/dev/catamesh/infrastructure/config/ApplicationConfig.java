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
import dev.catamesh.application.handler.*;
import dev.catamesh.application.strategy.ImmutabilityDataProductPolicyRuleStrategy;
import dev.catamesh.core.cqrs.Query;
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
import dev.catamesh.infrastructure.cqrs.db.OptionalResourceDefinitionVersionQuery;
import dev.catamesh.infrastructure.cqrs.db.OptionalResourceQuery;
import dev.catamesh.infrastructure.cqrs.db.UpdateDataProductCommand;
import dev.catamesh.infrastructure.cqrs.db.UpdateResourceCommand;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.util.Objects;

public class ApplicationConfig {

    private static final String CATAMESH = "catamesh";
    private static final String DEFAULT_H2_URL =
            "jdbc:h2:file:./db-file-catamesh/catamesh_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

    private final DataSource dataSource;

    private final ObjectMapper objectMapper;
    private final StartApplicationFacade startApplicationFacade;
    private final DataProductFacade dataProductFacade;
    private final TemplateFacade templateFacade;

    public ApplicationConfig() {
        this(createDefaultDataSource());
    }

    public ApplicationConfig(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource is required");
        this.objectMapper = createSerializationContext().jsonMapper();
        JsonYamlContext serialization = createSerializationContext();
        QueryCommandContext qc = createQueryCommandContext(this.dataSource, serialization.jsonMapper());
        PipelineContext pipelines = createPipelineContext(serialization, qc);

        this.startApplicationFacade = new DefaultStartApplicationFacade(qc.initTablesDBCommand());
        this.templateFacade = new DefaultTemplateFacade(qc.getFileFromResourceQuery());
        this.dataProductFacade = new DefaultDataProductFacade(
                pipelines.applyDataProductPipelineFactory(),
                pipelines.planDataProductPipelineFactory(),
                pipelines.diffDataProductPipelineFactory(),
                pipelines.planDestroyDataProductPipelineFactory(),
                pipelines.applyDestroyDataProductPipelineFactory(),
                qc.optionalDataProductQuery(),
                qc.allResourcesQuery(),
                qc.getResourceDefinitionQuery()
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

    public ObjectMapper jsonMapper() {
        return objectMapper;
    }

    public Query<String, String> getFileFromResourceQuery() {
        return new GetFileFromResourceQuery();
    }

    private static DataSource createDefaultDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DEFAULT_H2_URL);
        dataSource.setUser(CATAMESH);
        dataSource.setPassword("");
        return dataSource;
    }

    private JsonYamlContext createSerializationContext() {
        JSONConfig jsonConfig = new JSONConfig();
        YAMLConfig yamlConfig = new YAMLConfig();

        return new JsonYamlContext(
                jsonConfig.jsonMapper(),
                yamlConfig.yamlMapper(),
                jsonConfig.dataProductSchema(),
                jsonConfig.resourceSchema(),
                jsonConfig.bucketSchema()
        );
    }

    private QueryCommandContext createQueryCommandContext(DataSource dataSource, ObjectMapper jsonMapper) {
        GetFileFromResourceQuery getFileFromResourceQuery = new GetFileFromResourceQuery();

        return new QueryCommandContext(
                getFileFromResourceQuery,
                new CreateDataProductCommand(dataSource),
                new UpdateDataProductCommand(dataSource),
                new CreateResourceCommand(dataSource),
                new UpdateResourceCommand(dataSource),
                new CreateResourceDefinitionCommand(dataSource, jsonMapper),
                new DeactivateResourceDefinitionsByResourceIdCommand(dataSource),
                new DeleteDataProductCommand(dataSource),
                new DeleteResourceCommand(dataSource),
                new DeleteResourceDefinitionCommand(dataSource),
                new OptionalDataProductQuery(dataSource),
                new OptionalResourceQuery(dataSource),
                new OptionalResourceDefinitionQuery(dataSource),
                new OptionalResourceDefinitionVersionQuery(dataSource, jsonMapper),
                new AllResourcesQuery(dataSource),
                new CountResourceDefinitionsByResourceIdQuery(dataSource),
                new GetResourceDefinitionQuery(dataSource, jsonMapper),
                new InitTablesDBCommand(dataSource, getFileFromResourceQuery)
        );
    }

    private PipelineContext createPipelineContext(JsonYamlContext ctx, QueryCommandContext qc) {
        ApplyDataProductPipelineFactory applyDataProductPipelineFactory = new ApplyDataProductPipelineFactory(
                new YAMLToDataProductHandler(ctx.yamlMapper()),
                new ValidateDataProductSchemaHandler(ctx.dataProductSchema(), ctx.jsonMapper()),
                new ValidateResourceSchemaHandler(ctx.resourceSchema(), ctx.jsonMapper()),
                new ValidateBucketDefinitionSchemaHandler(ctx.bucketSchema(), ctx.jsonMapper()),
                new CheckIfExistDataProductHandler(qc.optionalDataProductQuery()),
                new ValidateDataProductUpdateHandler(),
                new UpdateDataProductHandler(qc.updateDataProductCommand()),
                new CreateDataProductHandler(qc.createDataProductCommand()),
                new CheckIfExistResourcesHandler(qc.optionalResourceQuery()),
                new UpdateResourcesHandler(qc.updateResourceCommand()),
                new CreateResourcesHandler(qc.createResourceCommand()),
                new CheckIfExistResourceDefinitionVersionHandler(
                        qc.optionalResourceDefinitionQuery(),
                        qc.optionalResourceQuery()
                ),
                new ValidateResourceDefinitionVersionImmutabilityHandler(
                        qc.optionalResourceDefinitionVersionQuery()
                ),
                new CreateResourceDefinitionsHandler(
                        qc.deactivateResourceDefinitionsByResourceIdCommand(),
                        qc.createResourceDefinitionCommand()
                )
        );

        PlanDataProductPipelineFactory planDataProductPipelineFactory = new PlanDataProductPipelineFactory(
                new YAMLToDataProductHandler(ctx.yamlMapper()),
                new ValidateDataProductSchemaHandler(ctx.dataProductSchema(), ctx.jsonMapper()),
                new ValidateResourceSchemaHandler(ctx.resourceSchema(), ctx.jsonMapper()),
                new ValidateBucketDefinitionSchemaHandler(ctx.bucketSchema(), ctx.jsonMapper()),
                new CheckIfExistDataProductHandler(qc.optionalDataProductQuery()),
                new ValidateDataProductUpdateHandler(),
                new CheckIfExistResourcesHandler(qc.optionalResourceQuery()),
                new PlanCheckResourceDefinitionVersionHandler(
                        qc.optionalResourceDefinitionQuery(),
                        qc.optionalResourceQuery()
                ),
                new ValidateResourceDefinitionVersionImmutabilityHandler(
                        qc.optionalResourceDefinitionVersionQuery()
                )
        );

        DiffDataProductPipelineFactory diffDataProductPipelineFactory = new DiffDataProductPipelineFactory(
                new YAMLToDataProductHandler(ctx.yamlMapper()),
                new ValidateDataProductSchemaHandler(ctx.dataProductSchema(), ctx.jsonMapper()),
                new ValidateResourceSchemaHandler(ctx.resourceSchema(), ctx.jsonMapper()),
                new ValidateBucketDefinitionSchemaHandler(ctx.bucketSchema(), ctx.jsonMapper()),
                new CheckIfExistDataProductHandler(qc.optionalDataProductQuery()),
                new CheckIfExistResourcesHandler(qc.optionalResourceQuery()),
                new PlanCheckResourceDefinitionVersionHandler(
                        qc.optionalResourceDefinitionQuery(),
                        qc.optionalResourceQuery()
                ),
                new LoadCurrentDataProductForDiffHandler(
                        qc.optionalDataProductQuery(),
                        qc.allResourcesQuery(),
                        qc.getResourceDefinitionQuery()
                ),
                new BuildDiffResultHandler(),
                new ImmutabilityDataProductPolicyRuleHandler(new ImmutabilityDataProductPolicyRuleStrategy(qc.optionalResourceDefinitionVersionQuery()))
        );

        PlanDestroyDataProductPipelineFactory planDestroyDataProductPipelineFactory =
                new PlanDestroyDataProductPipelineFactory(
                        new YAMLToDestroyDataProductHandler(ctx.yamlMapper()),
                        new ValidateDestroyDataProductSchemaHandler(ctx.dataProductSchema(), ctx.jsonMapper()),
                        new ValidateDestroyResourceSchemaHandler(ctx.resourceSchema(), ctx.jsonMapper()),
                        new ValidateDestroyBucketDefinitionSchemaHandler(ctx.bucketSchema(), ctx.jsonMapper()),
                        new ValidateDestroyDefinitionVersionHandler(),
                        new GetOptionalDataProductForDestroyHandler(qc.optionalDataProductQuery()),
                        new GetResourcesForDestroyHandler(qc.allResourcesQuery()),
                        new PlanDestroyDataProductHandler(
                                qc.optionalResourceDefinitionQuery(),
                                qc.countResourceDefinitionsByResourceIdQuery()
                        ),
                        new PlanDestroyTerminalHandler()
                );

        ApplyDestroyDataProductPipelineFactory applyDestroyDataProductPipelineFactory =
                new ApplyDestroyDataProductPipelineFactory(
                        new YAMLToDestroyDataProductHandler(ctx.yamlMapper()),
                        new ValidateDestroyDataProductSchemaHandler(ctx.dataProductSchema(), ctx.jsonMapper()),
                        new ValidateDestroyResourceSchemaHandler(ctx.resourceSchema(), ctx.jsonMapper()),
                        new ValidateDestroyBucketDefinitionSchemaHandler(ctx.bucketSchema(), ctx.jsonMapper()),
                        new ValidateDestroyDefinitionVersionHandler(),
                        new GetOptionalDataProductForDestroyHandler(qc.optionalDataProductQuery()),
                        new GetResourcesForDestroyHandler(qc.allResourcesQuery()),
                        new PlanDestroyDataProductHandler(
                                qc.optionalResourceDefinitionQuery(),
                                qc.countResourceDefinitionsByResourceIdQuery()
                        ),
                        new DestroyDataProductHandler(qc.deleteDataProductCommand(), qc.allResourcesQuery()),
                        new DestroyResourceHandler(qc.deleteResourceCommand()),
                        new DestroyResourceDefinitionHandler(qc.deleteResourceDefinitionCommand())
                );

        return new PipelineContext(
                applyDataProductPipelineFactory,
                planDataProductPipelineFactory,
                diffDataProductPipelineFactory,
                planDestroyDataProductPipelineFactory,
                applyDestroyDataProductPipelineFactory
        );
    }

    private record JsonYamlContext(
            ObjectMapper jsonMapper,
            ObjectMapper yamlMapper,
            Schema dataProductSchema,
            Schema resourceSchema,
            Schema bucketSchema
    ) {
    }

    private record QueryCommandContext(
            GetFileFromResourceQuery getFileFromResourceQuery,
            CreateDataProductCommand createDataProductCommand,
            UpdateDataProductCommand updateDataProductCommand,
            CreateResourceCommand createResourceCommand,
            UpdateResourceCommand updateResourceCommand,
            CreateResourceDefinitionCommand createResourceDefinitionCommand,
            DeactivateResourceDefinitionsByResourceIdCommand deactivateResourceDefinitionsByResourceIdCommand,
            DeleteDataProductCommand deleteDataProductCommand,
            DeleteResourceCommand deleteResourceCommand,
            DeleteResourceDefinitionCommand deleteResourceDefinitionCommand,
            OptionalDataProductQuery optionalDataProductQuery,
            OptionalResourceQuery optionalResourceQuery,
            OptionalResourceDefinitionQuery optionalResourceDefinitionQuery,
            OptionalResourceDefinitionVersionQuery optionalResourceDefinitionVersionQuery,
            AllResourcesQuery allResourcesQuery,
            CountResourceDefinitionsByResourceIdQuery countResourceDefinitionsByResourceIdQuery,
            GetResourceDefinitionQuery getResourceDefinitionQuery,
            InitTablesDBCommand initTablesDBCommand
    ) {
    }

    private record PipelineContext(
            ApplyDataProductPipelineFactory applyDataProductPipelineFactory,
            PlanDataProductPipelineFactory planDataProductPipelineFactory,
            DiffDataProductPipelineFactory diffDataProductPipelineFactory,
            PlanDestroyDataProductPipelineFactory planDestroyDataProductPipelineFactory,
            ApplyDestroyDataProductPipelineFactory applyDestroyDataProductPipelineFactory
    ) {
    }
}
