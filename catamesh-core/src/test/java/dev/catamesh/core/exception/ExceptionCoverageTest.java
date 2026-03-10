package dev.catamesh.core.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ExceptionCoverageTest {

    @Test
    void simpleExceptionsPreserveMessages() {
        Assertions.assertEquals("exists", new AlreadyExistsException("exists").getMessage());
        Assertions.assertEquals("conflict", new ConflictException("conflict").getMessage());
    }

    @Test
    void exceptionsWithCausePreserveMessageAndCause() {
        RuntimeException cause = new RuntimeException("boom");
        InvalidInputException invalidInputException = new InvalidInputException("invalid", cause);
        DependencyException dependencyException = new DependencyException("dependency", cause);
        MappingException mappingException = new MappingException("mapping", cause);

        Assertions.assertEquals("invalid", invalidInputException.getMessage());
        Assertions.assertSame(cause, invalidInputException.getCause());
        Assertions.assertEquals("dependency", dependencyException.getMessage());
        Assertions.assertSame(cause, dependencyException.getCause());
        Assertions.assertEquals("mapping", mappingException.getMessage());
        Assertions.assertSame(cause, mappingException.getCause());
    }

    @Test
    void schemaExceptionExposesErrors() {
        SchemaException schemaException = new SchemaException("schema", List.of("field=invalid"));

        Assertions.assertEquals("schema", schemaException.getMessage());
        Assertions.assertEquals(List.of("field=invalid"), schemaException.getErrors());
    }
}
