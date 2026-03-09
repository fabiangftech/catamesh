package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.infrastructure.dto.GetResourceDefinitionDTO;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
@Named("deleteResourceDefinitionCommand")
public class DeleteResourceDefinitionCommand implements Command<GetResourceDefinitionDTO, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteResourceDefinitionCommand.class);

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

            logger.error(message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}