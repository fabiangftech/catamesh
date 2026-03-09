package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.exception.InvariantException;
import cl.guaman.weave.core.model.Resource;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Singleton
@Named("deactivateResourceDefinitionsByResourceIdCommand")
public class DeactivateResourceDefinitionsByResourceIdCommand implements Command<Resource, Resource> {
    private static final Logger logger = LoggerFactory.getLogger(DeactivateResourceDefinitionsByResourceIdCommand.class);
    private static final String SQL_UPDATE = """
            UPDATE resource_definition
            SET active = false
            WHERE resource_id = ?
              AND active = true
            """;
    private final DataSource dataSource;

    public DeactivateResourceDefinitionsByResourceIdCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Resource execute(Resource resource) {
        if (Objects.isNull(resource.getId())) {
            throw new InvariantException(String.format("Resource id is required to deactivate definitions for resource=%s", resource.getName()));
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, resource.getId());
            preparedStatement.executeUpdate();
            return resource;
        } catch (SQLException e) {
            String message = String.format("Error deactivating resource definitions for resource(name=%s)", resource.getName());
            logger.error(message, e);
            throw new DependencyException(message, e);
        }
    }
}
