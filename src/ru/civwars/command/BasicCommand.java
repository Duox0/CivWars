package ru.civwars.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.civwars.CivWars;
import ru.civwars.i18n.I18n;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.bukkit.BukkitBlockCommandSender;
import ru.civwars.bukkit.BukkitConsoleCommandSender;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.util.EnumFacing;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BasicCommand implements org.bukkit.command.CommandExecutor {

    private static final int ENTRIES_IN_PAGE = 10;

    protected final JavaPlugin plugin;

    private final String fullpath;

    /* Псевдонимы команды ([foobar, fubar]). */
    private final String[] aliases;

    /* Краткое описание использования команды (Usage: '/<command> [test|stop]'). */
    private String usage;

    /* Краткое описание того, что делает команда. */
    private String description;

    /* Разрешение, необходимое для использования команды (test.foo). */
    private String permission;
    
    /* Определяет, скрыта ли команда в общем списке. */
    private boolean isHidden = false;

    private final Map<String, String> primaryAliases;
    private final Map<String, BasicCommand> subcommands;
    private final Set<RegisteredHandler> handlers;

    public BasicCommand(@NotNull JavaPlugin plugin, @NotNull String fullpath, @NotNull String[] aliases) {
        this.plugin = plugin;
        this.fullpath = fullpath;
        this.aliases = aliases;
        this.usage = "/" + this.aliases[0];
        this.description = "";
        this.permission = null;

        this.primaryAliases = Maps.newHashMap();
        this.subcommands = Maps.newHashMap();
        this.handlers = new HashSet<>();

        this.createRegisteredHandlers();
    }

    public BasicCommand(@NotNull JavaPlugin plugin, @NotNull String[] aliases) {
        this(plugin, aliases[0], aliases);
    }

    public BasicCommand(@NotNull CivWars plugin, @NotNull String name) {
        this(plugin, new String[]{name});
    }

    /**
     * Регистрирует новую подкоманду.
     *
     * @param handler
     */
    protected final void registerSubCommand(@NotNull BasicCommand handler) {
        for (String alias : handler.aliases) {
            if (!this.primaryAliases.containsKey(alias)) {
                this.primaryAliases.put(alias, handler.getPrimaryAlias());
            }
        }

        if (!this.subcommands.containsKey(handler.getPrimaryAlias())) {
            this.subcommands.put(handler.getPrimaryAlias(), handler);
        }
    }

    private void createRegisteredHandlers() {
        for (Method method : this.getClass().getMethods()) {
            if (!method.isAnnotationPresent(CommandHandler.class) || method.getParameterCount() == 0) {
                continue;
            }

            CommandHandler command = method.getAnnotation(CommandHandler.class);

            CommandExecutor executor = (@NotNull BasicCommand command1, Object[] objects) -> {
                try {
                    method.invoke(command1, objects);
                } catch (InvocationTargetException ex) {
                    throw new CommandException(ex.getCause());
                } catch (Throwable t) {
                    throw new CommandException(t);
                }
            };
            RegisteredHandler handler = new RegisteredHandler(this, executor, Arrays.asList(method.getParameters()));

            if (method.isAnnotationPresent(CommandPermissions.class)) {
                handler.setPermission(method.getAnnotation(CommandPermissions.class).value());
            }

            this.handlers.add(handler);
        }
    }

    /**
     * @return имя команды.
     */
    @NotNull
    public String getPrimaryAlias() {
        return this.aliases[0];
    }

    /**
     * @return список псевдонимов.
     */
    @NotNull
    public List<String> getAllAliases() {
        return Arrays.asList(this.aliases);
    }

    /**
     * Устанавливает краткое описание использования команды.
     *
     * @param usage краткое описание использования команды.
     */
    public final void setUsage(@NotNull String usage) {
        Validate.notNull(usage, "Usage can't be null");
        this.usage = usage;
    }

    /**
     * @return краткое описание использования команды.
     */
    @NotNull
    public final String getUsage() {
        return this.usage;
    }

    /**
     * Устанавливает краткое описание того, что делает команда.
     *
     * @param description краткое описание того, что делает команда.
     */
    public void setDescription(@NotNull String description) {
        Validate.notNull(description, "Description can't be null");
        this.description = description;
    }

    /**
     * Получает краткое описание того, что делает команда.
     *
     * @return краткое описание того, что делает команда.
     */
    @NotNull
    public String getDescription() {
        return this.description;
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
    
    /**
     * Устанавливает скрытность команды.
     * @param hide 
     */
    public final void setIsHidden(boolean hide) {
        this.isHidden = hide;
    }
    
    /**
     * Определяет, скрыта ли команда в общем списке.
     * @return {@code true}, если команда скрыта. Иначе {@code false}.
     */
    public final boolean isHidden() {
        return this.isHidden;
    }

    @Nullable
    protected final BasicCommand getCommandHandler(@NotNull String alias) {
        if (!this.primaryAliases.containsKey(alias)) {
            return null;
        }
        return this.subcommands.get(this.primaryAliases.get(alias));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String aliases, String[] args) {
        AbstractCommandSender sndr;

        if (sender instanceof Player) {
            sndr = PlayerManager.getPlayer((Player) sender);
            if (sndr == null) {
                sender.sendMessage(ChatColor.RED + I18n.tl("invalid_player_data"));
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sndr = new BukkitConsoleCommandSender((ConsoleCommandSender) sender);
        } else if (sender instanceof BlockCommandSender) {
            sndr = new BukkitBlockCommandSender((BlockCommandSender) sender);
        } else {
            return false;
        }

        this.run(sndr, args);
        return true;
    }

    protected final void run(@NotNull AbstractCommandSender sender, @NotNull String[] args) {
        if (this.getPermission() != null && !sender.hasPermission(this.getPermission())) {
            sender.sendError("dont_have_permissions_to_use_command");
            return;
        }

        if (args.length > 0) {
            if ("help".equalsIgnoreCase(args[0]) || "?".equalsIgnoreCase(args[0])) {
                Integer page = args.length > 1 ? toInt(args[1]) : 0;
                this.help(sender, page);
                return;
            }

            BasicCommand handler = this.getCommandHandler(args[0]);
            if (handler != null) {
                handler.run(sender, this.subargs(args, 1));
                return;
            }
        }

        if (this.handlers.isEmpty()) {
            this.help(sender);
            return;
        }

        boolean success = false;
        for (RegisteredHandler handler : this.handlers) {
            for (int i = 0; i < 1; i++) {
                List<Object> params = Lists.newLinkedList();
                int paramSize = 0; // required args.length + 1
                int paramCount = 0;

                List<Parameter> cmdParams = handler.getParameters();
                for (Parameter param : cmdParams) {
                    if (param.isAnnotationPresent(Nullable.class)) {
                        if (paramSize - 1 == args.length) {
                            break;
                        }
                    }

                    paramSize++;
                    paramCount++;
                }

                if (paramSize - 1 > args.length) {
                    continue;
                }

                if ((sender instanceof KPlayer) && cmdParams.get(0).getType().equals(KPlayer.class)) {
                    params.add(sender);
                } else if (cmdParams.get(0).getType().equals(AbstractCommandSender.class)) {
                    params.add(sender);
                } else {
                    continue;
                }

                boolean invalidArg = false;

                Object obj = null;

                int ai = 0;
                for (int pi = 1; pi < paramCount && !invalidArg; pi++) {

                    String arg = "";
                    if (pi == paramCount - 1 && paramSize - 1 < args.length) {
                        // last arg
                        for (; ai < args.length; ai++) {
                            arg += args[ai];
                            if (ai < args.length - 1) {
                                arg += " ";
                            }
                        }
                    } else {
                        arg = args[ai];
                    }

                    Parameter param = cmdParams.get(pi);
                    Class<?> paramClass = param.getType();

                    if (paramClass.equals(String.class)) {
                        params.add(arg);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Boolean.class) && (obj = this.toBoolean(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Byte.class) && (obj = this.toByte(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Short.class) && (obj = this.toShort(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Integer.class) && (obj = this.toInt(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Long.class) && (obj = this.toLong(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Float.class) && (obj = this.toFloat(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(Double.class) && (obj = this.toDouble(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(KPlayer.class) && (obj = PlayerManager.getPlayer(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    } else if (paramClass.equals(EnumFacing.class) && (obj = this.toFacing(arg)) != null) {
                        params.add(obj);
                        ai++;
                        continue;
                    }
                    invalidArg = true;
                }

                if (!invalidArg) {
                    for (int r = paramCount; r < cmdParams.size(); r++) {
                        if (cmdParams.get(r).isAnnotationPresent(Nullable.class)) {
                            params.add(null);
                        } else {
                            invalidArg = false;
                            break;
                        }
                    }

                    if (!invalidArg) {
                        try {
                            handler.handle(params.toArray(new Object[params.size()]));
                            return;
                        } catch (Throwable ex) {
                            Logger.getLogger(BasicCommand.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        sender.sendMessage(ChatColor.WHITE + "Usage: " + this.getUsage());
    }

    protected final void help(@NotNull AbstractCommandSender sender, int page) {
        List<BasicCommand> subcommands = Lists.newArrayList(this.subcommands.values().stream().filter(cmd -> !cmd.isHidden()).collect(Collectors.toList()));
        if (subcommands.isEmpty()) {
            return;
        }

        int totalPage = (subcommands.size() - 1) / ENTRIES_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * ENTRIES_IN_PAGE, subcommands.size());

        sender.sendHeader("commands_" + this.fullpath + "_help_header", (page + 1), (totalPage + 1));
        for (int i = (page * ENTRIES_IN_PAGE); i < lastIndex; i++) {
            BasicCommand command = subcommands.get(i);
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.LIGHT_PURPLE);
            
            boolean prevCode = false;
            String[] cmd = StringUtils.split(command.getUsage(), " ");
            for (String str : cmd) {
                if(str.charAt(0) == '<' || str.charAt(0) == '[') {
                    if(!prevCode) {
                        prevCode = true;
                        builder.append(ChatColor.YELLOW);
                    }
                }
                builder.append(str).append(" ");
            }

            builder.append(ChatColor.GRAY).append(": ").append(I18n.tl("commands_" + command.fullpath + "_desc"));

            sender.sendMessage(builder.toString());
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_generic_help_footer", this.fullpath.replaceAll("_", " "));
        }
    }

    protected final void help(@NotNull AbstractCommandSender sender) {
        this.help(sender, 1);
    }

    /**
     * Возвращает новый массив аргументов, который начинается с аргумента,
     * заданного индексом, и продолжается до конца данного массива.
     *
     * @param args - аргументы.
     * @param firstIndex - начальный индекс, включительно.
     * @return
     */
    @NotNull
    protected final String[] subargs(String[] args, int firstIndex) {
        Validate.notNull(args);

        if ((args.length - firstIndex) <= 0) {
            return new String[0];
        }

        String[] out = new String[args.length - firstIndex];
        for (int j = 0, length = out.length; j < length; j++) {
            out[j] = args[j + firstIndex];
        }
        return out;
    }

    private Boolean toBoolean(String value) {
        if (value == null) {
            return null;
        }

        value = value.toLowerCase();
        switch (value) {
            case "true":
            case "1":
                return new Boolean(true);
            case "false":
            case "0":
                return new Boolean(false);
        }
        return null;
    }

    private Byte toByte(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Short toShort(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer toInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long toLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Float toFloat(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double toDouble(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private EnumFacing toFacing(String value) {
        if (value == null) {
            return null;
        }

        EnumFacing facing = null;
        try {
            facing = EnumFacing.getFront(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            facing = EnumFacing.byName(value);
        }
        return facing != null ? facing : EnumFacing.EAST;
    }

}
