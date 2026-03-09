package dev.catamesh.application.facade;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.facade.StartApplicationFacade;

public class DefaultStartApplicationFacade implements StartApplicationFacade {

    private final Command<Void, Void> initTablesDBCommand;

    public DefaultStartApplicationFacade(Command<Void, Void> initTablesDBCommand) {
        this.initTablesDBCommand = initTablesDBCommand;
    }

    @Override
    public void start() {
        initTablesDBCommand.execute();
    }
}
