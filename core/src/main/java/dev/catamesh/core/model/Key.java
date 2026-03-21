package dev.catamesh.core.model;

import java.util.UUID;

public record Key(String value) {

    public static Key newId(){
        return new Key(UUID.randomUUID().toString());
    }


    public static Key create(String value){
        return new Key(value);
    }
}
