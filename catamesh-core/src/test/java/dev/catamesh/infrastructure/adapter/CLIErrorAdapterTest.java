package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.exception.ConflictException;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.ImmutableResourceDefinitionVersionException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.exception.SchemaException;
import dev.catamesh.infrastructure.dto.CLIErrorPayloadDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CLIErrorAdapterTest {

    @Test
    void mapSchemaExceptionToValidationError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new SchemaException(
                "Invalid resource schema",
                List.of("$.resources[0].name is required")
        ));

        Assertions.assertEquals("VALIDATION_ERROR", payload.getErrorCode());
        Assertions.assertEquals(20, payload.getStatus());
        Assertions.assertEquals("Schema validation failed", payload.getTitle());
        Assertions.assertEquals(List.of("$.resources[0].name is required"), payload.getDetails());
    }

    @Test
    void mapImmutableDefinitionExceptionToConflictErrorWithHint() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new ImmutableResourceDefinitionVersionException(
                "component-1",
                "0.0.3",
                List.of("config")
        ));

        Assertions.assertEquals("CONFLICT_ERROR", payload.getErrorCode());
        Assertions.assertEquals(21, payload.getStatus());
        Assertions.assertEquals("Definition version is immutable", payload.getTitle());
        Assertions.assertTrue(payload.getMessage().contains("component-1"));
        Assertions.assertTrue(payload.getHint().contains("Bump definition.version"));
        Assertions.assertEquals(List.of("config"), payload.getDetails());
    }

    @Test
    void mapNotFoundExceptionToNotFoundError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new NotFoundException("data product not found"));

        Assertions.assertEquals("NOT_FOUND_ERROR", payload.getErrorCode());
        Assertions.assertEquals(22, payload.getStatus());
    }

    @Test
    void mapDependencyExceptionToDependencyError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new DependencyException("database not available"));

        Assertions.assertEquals("DEPENDENCY_ERROR", payload.getErrorCode());
        Assertions.assertEquals(23, payload.getStatus());
    }

    @Test
    void mapInvariantExceptionToInvariantError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new InvariantException("resource id is required"));

        Assertions.assertEquals("INVARIANT_ERROR", payload.getErrorCode());
        Assertions.assertEquals(24, payload.getStatus());
    }

    @Test
    void mapUnexpectedExceptionToInternalError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new MappingException("failed to map resource definition"));

        Assertions.assertEquals("INTERNAL_ERROR", payload.getErrorCode());
        Assertions.assertEquals(25, payload.getStatus());
    }

    @Test
    void mapPlainConflictExceptionToConflictError() {
        CLIErrorPayloadDTO payload = CLIErrorAdapter.map(new ConflictException("resource kind cannot be changed"));

        Assertions.assertEquals("CONFLICT_ERROR", payload.getErrorCode());
        Assertions.assertEquals(21, payload.getStatus());
        Assertions.assertEquals("Conflict", payload.getTitle());
    }
}
