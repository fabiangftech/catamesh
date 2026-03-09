package dev.catamesh.infrastructure.dto;


import java.util.Collections;
import java.util.List;

public record ErrorApiDTO(String message, List<String> errors) {

    public static ErrorApiDTO create(String message) {
        return new ErrorApiDTO(message, Collections.emptyList());
    }
    public static ErrorApiDTO create(String message,List<String> errors) {
        return new ErrorApiDTO(message, errors);
    }

}
