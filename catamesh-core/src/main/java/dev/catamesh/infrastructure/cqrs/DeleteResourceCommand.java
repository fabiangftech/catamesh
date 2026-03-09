package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.Key;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteResourceCommand implements Command<Key, Void> {

    private static final Logger logger = Logger.getLogger(DeleteResourceCommand.class.getName());

    private static final String SQL_DELETE = """
            DELETE FROM resource
            WHERE id = ?
            """;

    private final DataSource dataSource;

    public DeleteResourceCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Void execute(Key key) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {

            preparedStatement.setString(1, key.value());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            String message = String.format(
                    "Database error deleting resource (resourceId=%s)",
                    key.value()
            );

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}