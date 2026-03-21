package dev.catamesh.infrastructure.adapter;

import tools.jackson.databind.ObjectMapper;

public final class CLIJsonAdapter {

    private CLIJsonAdapter() {
        // utility class
    }

    public static String toJson(Object payload, ObjectMapper jsonMapper) {
        return jsonMapper.writeValueAsString(payload);
    }
}
