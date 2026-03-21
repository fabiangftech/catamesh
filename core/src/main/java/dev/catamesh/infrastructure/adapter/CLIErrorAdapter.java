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
import java.util.Optional;

public final class CLIErrorAdapter {
    private static final String SCHEMA_VALIDATION_MESSAGE = "The provided YAML does not match the expected schema.";

    private CLIErrorAdapter(){
        // do nothing
    }

    public static CLIErrorPayloadDTO map(Throwable error) {
        return immutableDefinitionPayload(error)
                .or(() -> schemaPayload(error))
                .or(() -> invalidInputPayload(error))
                .or(() -> conflictPayload(error))
                .or(() -> notFoundPayload(error))
                .or(() -> dependencyPayload(error))
                .or(() -> invariantPayload(error))
                .or(() -> mappingPayload(error))
                .orElseGet(() -> internalPayload(error));
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

    private static Optional<CLIErrorPayloadDTO> immutableDefinitionPayload(Throwable error) {
        if (!(error instanceof ImmutableResourceDefinitionVersionException immutableError)) {
            return Optional.empty();
        }

        return Optional.of(new CLIErrorPayloadDTO(
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
        ));
    }

    private static Optional<CLIErrorPayloadDTO> schemaPayload(Throwable error) {
        if (!(error instanceof SchemaException schemaError)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.VALIDATION_ERROR,
                "Schema validation failed",
                SCHEMA_VALIDATION_MESSAGE,
                null,
                schemaError.getErrors()
        ));
    }

    private static Optional<CLIErrorPayloadDTO> invalidInputPayload(Throwable error) {
        if (!(error instanceof InvalidInputException)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.VALIDATION_ERROR,
                "Invalid input",
                safeMessage(error),
                null,
                null
        ));
    }

    private static Optional<CLIErrorPayloadDTO> conflictPayload(Throwable error) {
        if (!(error instanceof ConflictException) && !(error instanceof AlreadyExistsException)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.CONFLICT_ERROR,
                CLIErrorCodeDTO.CONFLICT_ERROR.getTitle(),
                safeMessage(error),
                null,
                null
        ));
    }

    private static Optional<CLIErrorPayloadDTO> notFoundPayload(Throwable error) {
        if (!(error instanceof NotFoundException)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.NOT_FOUND_ERROR,
                CLIErrorCodeDTO.NOT_FOUND_ERROR.getTitle(),
                safeMessage(error),
                null,
                null
        ));
    }

    private static Optional<CLIErrorPayloadDTO> dependencyPayload(Throwable error) {
        if (!(error instanceof DependencyException)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.DEPENDENCY_ERROR,
                CLIErrorCodeDTO.DEPENDENCY_ERROR.getTitle(),
                safeMessage(error),
                null,
                null
        ));
    }

    private static Optional<CLIErrorPayloadDTO> invariantPayload(Throwable error) {
        if (!(error instanceof InvariantException)) {
            return Optional.empty();
        }

        return Optional.of(payload(
                CLIErrorCodeDTO.INVARIANT_ERROR,
                CLIErrorCodeDTO.INVARIANT_ERROR.getTitle(),
                safeMessage(error),
                null,
                null
        ));
    }

    private static Optional<CLIErrorPayloadDTO> mappingPayload(Throwable error) {
        if (!(error instanceof MappingException)) {
            return Optional.empty();
        }

        return Optional.of(internalPayload(error));
    }

    private static CLIErrorPayloadDTO internalPayload(Throwable error) {
        return payload(
                CLIErrorCodeDTO.INTERNAL_ERROR,
                CLIErrorCodeDTO.INTERNAL_ERROR.getTitle(),
                safeMessage(error),
                null,
                null
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
