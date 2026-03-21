package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.exception.InvalidInputException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceKind;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ResourceAdapter {

    private ResourceAdapter(){
        // do nothing
    }

    public static Resource toResource(ResultSet resultSet) throws SQLException {
        try {
            return new Resource(
                    Key.create(resultSet.getString("id")),
                    Key.create(resultSet.getString("data_product_id")),
                    resultSet.getString("name"),
                    resultSet.getString("display_name"),
                    ResourceKind.fromValue(resultSet.getString("kind"))
            );
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid resource kind in resource row", ex);
        }
    }
}
