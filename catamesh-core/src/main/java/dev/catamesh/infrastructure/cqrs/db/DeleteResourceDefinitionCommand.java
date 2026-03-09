package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteResourceDefinitionCommand implements Command<GetResourceDefinitionDTO, Void> {

    private static final Logger logger = Logger.getLogger(DeleteResourceDefinitionCommand.class.getName());

    private static final String SQL_DELETE = """
            DELETE FROM resource_definition
            WHERE resource_id = ?
              AND version = ?
            """;

    private final DataSource dataSource;

    public DeleteResourceDefinitionCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Void execute(GetResourceDefinitionDTO dto) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {

            preparedStatement.setString(1, dto.resourceId());
            preparedStatement.setString(2, dto.version());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String message = String.format(
                    "Database error deleting resource definition (resourceId=%s, version=%s)",
                    dto.resourceId(),
                    dto.version()
            );

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}