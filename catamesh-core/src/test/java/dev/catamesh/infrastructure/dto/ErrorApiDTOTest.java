package dev.catamesh.infrastructure.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ErrorApiDTOTest {

    @Test
    void createFactoriesPopulateErrors() {
        ErrorApiDTO withoutErrors = ErrorApiDTO.create("message");
        ErrorApiDTO withErrors = ErrorApiDTO.create("message", List.of("field=invalid"));

        Assertions.assertEquals("message", withoutErrors.message());
        Assertions.assertTrue(withoutErrors.errors().isEmpty());
        Assertions.assertEquals("message", withErrors.message());
        Assertions.assertEquals(List.of("field=invalid"), withErrors.errors());
    }
}
