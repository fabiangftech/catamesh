package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.model.Resource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateResourceCommand implements Command<Resource, Resource> {
    private static final Logger logger = Logger.getLogger(UpdateResourceCommand.class.getName());
    private static final String SQL_UPDATE = """
            UPDATE resource
            SET display_name = ?
            WHERE id = ?
            """;
    private final DataSource dataSource;

    public UpdateResourceCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Resource execute(Resource resource) {
        if (Objects.isNull(resource.getId())) {
            throw new InvariantException(String.format("Resource id is required to update resource=%s", resource.getName()));
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, resource.getDisplayName());
            preparedStatement.setString(2, resource.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error updating resource(name=%s)", resource.getName());
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        }
        return resource;
    }
}
