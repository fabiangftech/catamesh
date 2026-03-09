package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDataProductCommand implements Command<DataProduct, DataProduct> {
    private static final Logger logger = Logger.getLogger(CreateDataProductCommand.class.getName());
    private static final String SQL_INSERT = """
                INSERT INTO data_product
                (id, schema_version, name, display_name, kind, domain, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private final DataSource dataSource;

    public CreateDataProductCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataProduct execute(DataProduct dataProduct) {
        Key key = Key.newId();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, key.value());
            preparedStatement.setString(2, dataProduct.getSchemaVersion().getValue());
            preparedStatement.setString(3, dataProduct.getMetadata().getName());
            preparedStatement.setString(4, dataProduct.getMetadata().getDisplayName());
            preparedStatement.setString(5, dataProduct.getSpec().getKind().getValue());
            preparedStatement.setString(6, dataProduct.getMetadata().getDomain());
            preparedStatement.setString(7, dataProduct.getMetadata().getDescription());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error saving data product(name=%s)", dataProduct.getMetadata().getName());
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        }
        dataProduct.getMetadata().setId(key);
        return dataProduct;
    }
}
