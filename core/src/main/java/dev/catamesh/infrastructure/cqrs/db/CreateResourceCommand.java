package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateResourceCommand implements Command<Resource, Void> {
    private static final Logger logger = Logger.getLogger(CreateResourceCommand.class.getName());

    private static final String SQL_INSERT = """
            INSERT INTO resource
            (id, data_product_id, name, display_name, kind)
            VALUES (?, ?, ?, ?, ?)
            """;
    private final DataSource dataSource;

    public CreateResourceCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Void execute(Resource resource) {
        Key resourceId = Key.newId();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, resourceId.value());
            preparedStatement.setString(2, resource.getDataProductId());
            preparedStatement.setString(3, resource.getName());
            preparedStatement.setString(4, resource.getDisplayName());
            preparedStatement.setString(5, resource.getKind().getValue());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error saving resource(name=%s)", resource.getName());
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        }
        resource.setId(resourceId);
        return null;
    }
}
