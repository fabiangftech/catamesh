package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.exception.AlreadyExistsException;
import dev.catamesh.core.exception.ConflictException;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.ImmutableResourceDefinitionVersionException;
import dev.catamesh.core.exception.InvalidInputException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.infrastructure.dto.CLIErrorCodeDTO;
import dev.catamesh.infrastructure.dto.CLIErrorPayloadDTO;

import java.util.List;

public final class CLIErrorAdapter {

    private CLIErrorAdapter(){
        // do nothing
    }

    public static CLIErrorPayloadDTO map(Throwable error) {
        Throwable resolved = error;

        if (resolved instanceof ImmutableResourceDefinitionVersionException immutableError) {
            return new CLIErrorPayloadDTO(
                    CLIErrorCodeDTO.CONFLICT_ERROR.getValue(),
                    CLIErrorCodeDTO.CONFLICT_ERROR.getStatus(),
                    "Definition version is immutable",
                    String.format(
                            "Resource definition %s version %s already exists and cannot be changed in place.",
                            immutableError.getResourceName(),
                            immutableError.getVersion()
                    ),
                    "Bump definition.version to a new value, for example 0.0.99, and run cata apply again.",
                    immutableError.getChangedParts()
            );
        }

        if (resolved instanceof SchemaException schemaError) {
            return payload(
                    CLIErrorCodeDTO.VALIDATION_ERROR,
                    "Schema validation failed",
                    safeMessage(schemaError),
                    null,
                    schemaError.getErrors()
            );
        }

        if (resolved instanceof InvalidInputException) {
            return payload(
                    CLIErrorCodeDTO.VALIDATION_ERROR,
                    "Invalid input",
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        if (resolved instanceof ConflictException || resolved instanceof AlreadyExistsException) {
            return payload(
                    CLIErrorCodeDTO.CONFLICT_ERROR,
                    CLIErrorCodeDTO.CONFLICT_ERROR.getTitle(),
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        if (resolved instanceof NotFoundException) {
            return payload(
                    CLIErrorCodeDTO.NOT_FOUND_ERROR,
                    CLIErrorCodeDTO.NOT_FOUND_ERROR.getTitle(),
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        if (resolved instanceof DependencyException) {
            return payload(
                    CLIErrorCodeDTO.DEPENDENCY_ERROR,
                    CLIErrorCodeDTO.DEPENDENCY_ERROR.getTitle(),
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        if (resolved instanceof InvariantException) {
            return payload(
                    CLIErrorCodeDTO.INVARIANT_ERROR,
                    CLIErrorCodeDTO.INVARIANT_ERROR.getTitle(),
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        if (resolved instanceof MappingException) {
            return payload(
                    CLIErrorCodeDTO.INTERNAL_ERROR,
                    CLIErrorCodeDTO.INTERNAL_ERROR.getTitle(),
                    safeMessage(resolved),
                    null,
                    null
            );
        }

        return payload(
                CLIErrorCodeDTO.INTERNAL_ERROR,
                CLIErrorCodeDTO.INTERNAL_ERROR.getTitle(),
                safeMessage(resolved),
                null,
                null
        );
    }

    private static CLIErrorPayloadDTO payload(
            CLIErrorCodeDTO code,
            String title,
            String message,
            String hint,
            List<String> details) {
        return new CLIErrorPayloadDTO(
                code.getValue(),
                code.getStatus(),
                title,
                message,
                hint,
                details
        );
    }

    private static String safeMessage(Throwable error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) {
            return "Unexpected error";
        }
        return message;
    }
}
