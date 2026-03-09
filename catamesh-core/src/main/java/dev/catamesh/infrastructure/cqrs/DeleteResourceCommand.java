package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.model.Key;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
@Named("deleteResourceCommand")
public class DeleteResourceCommand implements Command<Key, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteResourceCommand.class);

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

            logger.error(message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}