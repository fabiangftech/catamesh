package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.NotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GetFileFromResourceQuery implements Query<String, String> {

    private static final Logger logger = Logger.getLogger(GetFileFromResourceQuery.class.getName());

    @Override
    public String execute(String path) {

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {

            if (is == null) {
                throw new NotFoundException(
                        String.format("Resource file not found (path=%s)", path)
                );
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {

            String message = String.format(
                    "Error reading resource file (path=%s)",
                    path
            );

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }
    }
}