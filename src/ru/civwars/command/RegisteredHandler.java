package ru.civwars.command;

import com.google.common.collect.Lists;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RegisteredHandler {
    
    private final BasicCommand command;
    private final CommandExecutor executor;

    /* Входящие параметры команды. */
    private List<Parameter> parameters = Lists.newArrayList();

    /* Разрешение, необходимое для использования команды. */
    private String permission = null;

    public RegisteredHandler(@NotNull BasicCommand command, @NotNull CommandExecutor executor, @NotNull List<Parameter> parameters) {
        this.command = command;
        this.executor = executor;
        this.parameters = Collections.unmodifiableList(parameters);
    }

    @NotNull
    public BasicCommand getCommand() {
        return this.command;
    }

    @NotNull
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    /**
     * Устанавливает разрешение, необходимое для использования команды.
     *
     * @param permission разрешение.
     */
    public final void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    /**
     * Получает разрешение, необходимое для использования команды.
     *
     * @return разрешение или {@code null}, если не задано.
     */
    @Nullable
    public final String getPermission() {
        return this.permission;
    }

    public void handle(Object[] objects) throws CommandException {
        this.executor.execute(this.command, objects);
    }

}
