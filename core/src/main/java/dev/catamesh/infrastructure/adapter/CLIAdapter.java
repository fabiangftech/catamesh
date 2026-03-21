package dev.catamesh.infrastructure.adapter;

import java.util.stream.IntStream;

public final class CLIAdapter {

    private CLIAdapter(){
        // do nothing
    }

    public static String[] removeKind(String[]command){
        return IntStream.range(0, command.length).filter(i -> i != 1)
                    .mapToObj(i -> command[i])
                    .toArray(String[]::new);
    }
}
