package ru.civwars.command;

import ru.lib27.annotation.NotNull;

public interface CommandExecutor {
    
    void execute(@NotNull BasicCommand command, Object[] objects) throws CommandException;
    
}
