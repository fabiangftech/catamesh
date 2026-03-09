package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.exception.NotFoundException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Singleton
public class GetFileFromResourceQuery implements Query<String, String> {

    private static final Logger logger = LoggerFactory.getLogger(GetFileFromResourceQuery.class);

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

            logger.error(message, e);

            throw new DependencyException(message);
        }
    }
}