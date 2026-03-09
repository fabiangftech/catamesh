package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.model.Resource;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class DeactivateResourceDefinitionsByResourceIdCommand implements Command<Resource, Resource> {
    private static final Logger logger = Logger.getLogger(DeactivateResourceDefinitionsByResourceIdCommand.class.getName());
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
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message, e);
        }
    }
}
