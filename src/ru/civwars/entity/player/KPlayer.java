package ru.civwars.entity.player;

import java.sql.SQLException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.i18n.I18n;
import ru.civwars.TaskMaster;
import ru.civwars.building.types.BuildingData;
import ru.civwars.building.validation.StructureValidator;
import ru.civwars.building.validation.StructureValidatorResult;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.entity.PlayerEntity;
import ru.civwars.entity.template.PlayerTemplate;
import ru.civwars.civ.Civilization;
import ru.civwars.database.Database;
import ru.civwars.menu.Menu;
import ru.civwars.menu.MenuItem;
import ru.civwars.menu.MenuManager;
import ru.civwars.network.SystemMessage;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.town.Town;
import ru.civwars.util.BlockPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.LocationUtils;
import ru.civwars.world.CivWorld;
import ru.databaseapi.database.DatabaseRow;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class KPlayer extends PlayerEntity implements AbstractCommandSender {

    private static final PlayerTemplate BASE_TEMPLATE;

    static {
        PlayerTemplate.Property property = new PlayerTemplate.Property(1, "base_template");
        property.baseHealth(20.0D)
                .baseDefense(50.0D)
                .baseDefenseToughness(0.0D)
                .baseAttackDamage(10.0D)
                .baseAttackSpeed(4.0D)
                .baseMovementSpeed(0.23D)
                .baseFlyingSpeed(0.4D)
                .baseKnockbackResistance(0.0D);

        BASE_TEMPLATE = new PlayerTemplate(property);
    }

    public static class Signature {

        public String name;
        public int typeId;
        public UUID worldId;
        public int tileX;
        public int tileZ;

    }

    protected final Random random = new Random();

    /* Золото игрока. */
    private long gold = 0;

    /* Время первого захода на сервер. */
    private long createdTime = 0;

    /* Время последнего входа на сервер. */
    private long lastLoginTime = 0;

    /* Время последнего выхода с сервера. */
    private long lastLogoutTime = 0;

    /* Последний IP-адрес, с которого выполнялось подключение к игроку. */
    private String lastLoginAddress = "";

    /* The inventory. */
    private final PlayerInventory inventory;

    /* Город, в который приглашен игрок. */
    private UUID townIdInvited = null;
    
    /* Город, в котором состоит игрок. */
    private Town town = null;

    private Menu currentMenu = null;
    private int currentPage = 1;

    private ScheduledFuture<?> autoSaveTask = null;

    /**
     * Создает нового игрока с заданной Bukkit-сущностью.
     *
     * Позже, объект должен быть инициализирован вызовом {@link #create()} или
     * {@link #loadFromDB(DatabaseRow)}.
     *
     * @param player Bukkit-объект игрока.
     */
    public KPlayer(@NotNull Player player) {
        super(player, BASE_TEMPLATE);
        this.setName(player.getName());

        this.inventory = new PlayerInventory(this);
    }

    /**
     * Инициализирует этого игрока и сохраняет в базу данных.
     *
     * @return {@code true}, если игрок сохранен. Иначе {@code false}.
     */
    public boolean create() {
        CivLogger.log(Level.INFO, "Creating player [{0}]", new Object[]{this.getObjectId()});

        long now = System.currentTimeMillis();
        this.createdTime = now;
        this.lastLoginTime = now;
        this.lastLoginAddress = this.getEntity().getAddress().getAddress().getHostAddress();

        boolean ok = Database.getConnection().execute("INSERT INTO players (PlayerId,Name,Gold,CreatedDate,LastLoginDate,LastLoginAddress) VALUES(?,?,?,?,?,?)",
                this.getObjectId(),
                this.getName(),
                this.getGold(),
                this.getCreatedTime(),
                this.getLastLoginTime(),
                this.getLastLoginAddress()
        );

        return ok;
    }

    @Override
    public void setName(@NotNull String name) {
        super.setName(name);
        PlayerCache.updatePlayerName(this.getObjectId(), name);
    }

    /**
     * Получает количество золота игрока.
     *
     * @return количество золота.
     */
    public long getGold() {
        return this.gold;
    }

    /**
     * Изменяет количество золота игрока.
     *
     * @param amount величина, на которую необходимо изменить золото.
     */
    public void changeGold(long amount) {
        if (amount > 0) {
            // deposit
            if (this.gold + amount > Long.MAX_VALUE) {
                amount = Long.MAX_VALUE - this.gold;
            }

            if (amount > 0) {
                this.gold += amount;
                Database.getConnection().asyncExecute("UPDATE players SET Gold=? WHERE PlayerId=?", this.getGold(), this.getObjectId());
            }
        } else if (amount < 0) {
            // withdraw
            if (this.gold + amount < 0) {
                amount = -this.gold;
            }

            if (amount < 0) {
                this.gold += amount;
                Database.getConnection().asyncExecute("UPDATE players SET Gold=? WHERE PlayerId=?", this.getGold(), this.getObjectId());
            }
        }
    }

    /**
     * Получает время первого входа игрока на сервер.
     *
     * @return время.
     */
    public long getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Получает время последнего входа на сервер.
     *
     * @return время.
     */
    public long getLastLoginTime() {
        return this.lastLoginTime;
    }

    /**
     * Получает время последнего выхода с сервера.
     *
     * @return время.
     */
    public long getLastLogoutTime() {
        return this.lastLogoutTime;
    }

    /**
     * Получает последний IP-адрес, с которого выполнялось подключение к игроку.
     *
     * @return IP-адрес.
     */
    @NotNull
    public String getLastLoginAddress() {
        return this.lastLoginAddress;
    }

    /**
     * Устанавливает город, в который приглашен игрок.
     * @param townId идентификатор города.
     */
    public void setTownInvited(@Nullable UUID townId) {
        this.townIdInvited = townId;
    }
    
    
    /**
     * Получает идентификатор города, в который приглашен игрок.
     * @return идентификатор города или {@code null}, если не задан.
     */
    @Nullable
    public UUID getTownInvited() {
        return this.townIdInvited;
    }
    
    /**
     * Устанавливает город, в котором состоит игрок.
     *
     * @param town город или {@code null}, если игрок не состоит в городе.
     */
    public void setTown(@Nullable Town town) {
        this.town = town;
        PlayerCache.updatePlayerTown(this.getObjectId(), town != null ? town.getId() : null);
    }

    /**
     * Получает город, в котором состоит игрок.
     *
     * @return город или {@code null}, если игрок не состоит в городе.
     */
    @Nullable
    public Town getTown() {
        return this.town;
    }

    /**
     * Получает цивилизацию, в которой состоит игрок.
     *
     * @return цивилизация.
     */
    @Nullable
    public Civilization getCiv() {
        return this.town != null ? this.town.getCiv() : null;
    }

    @Override
    protected void initStats() {
    }

    @Override
    public void init() {
        this.inventory.restore();
        super.init();
    }

    /**
     * @return инвентарь игрока.
     */
    @NotNull
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public void onOpenMenu(@NotNull Menu menu, int page) {
        this.currentMenu = menu;
        this.currentPage = Math.max(1, page);
    }

    public void onCloseMenu() {
        if (this.currentMenu != null) {
            this.currentMenu = null;
            this.currentPage = 1;
        }
    }

    /**
     * @return текущее меню, или {@code null}, если не задано.
     */
    @Nullable
    public Menu getCurrentMenu() {
        return this.currentMenu;
    }

    /**
     * @return текущая страница меню.
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    private Material[] materials = new Material[]{Material.SMOOTH_BRICK, Material.NETHERRACK, Material.PRISMARINE, Material.SANDSTONE};
    private String[] names = new String[]{"default", "nether", "desert", "jungle", "simple"};

    public void build(@NotNull BuildingData data, @NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing facing, @Nullable Consumer<StructureValidatorResult> consumer) {
        Menu menu = MenuManager.get().createNewMenu("Themes", 6);

        for (int i = 0; i < 15; i++) {
            menu.addItem(new MenuItem(this.materials[this.random.nextInt(this.materials.length)],
                    this.names[this.random.nextInt(this.names.length)], (p, clickType, slot) -> {
                p.sendRawMessage("Ты нажал на книжечку, что слева!");
                Schematic schematic = SchematicManager.getSchematic(data.getSchematicPath("default", facing));
                if (schematic == null) {
                    this.sendError("message_invalidSchematic");
                    return;
                }

                BlockPos corner = LocationUtils.repositionCorner(pos, facing, schematic.getSize().width, schematic.getSize().length);
                corner = new BlockPos(corner.getX(), corner.getY() + schematic.getYShift(), corner.getZ());
                this.build(data, world, corner, facing, schematic, consumer);
            }));
        }
        MenuManager.get().open(menu, this, 1);
    }

    protected void build(@NotNull BuildingData data, @NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing facing, @NotNull Schematic schematic, @Nullable Consumer<StructureValidatorResult> consumer) {
        this.sendRawMessage("buildable_preview_heading");
        this.sendRawMessage("buildable_preview_prompt1");
        this.sendRawMessage("buildable_preview_prompt2");

        this.startPreviewTask(world, pos, schematic);
        StructureValidator.validate(this, schematic, this.getWorld(), pos, consumer);
    }

    public void startPreviewTask(@NotNull CivWorld world, @NotNull BlockPos pos, @NotNull Schematic schematic) {
        System.out.println("startPreviewTask");
        this.stopPreviewTask();
    }

    public void stopPreviewTask() {
        System.out.println("stopPreviewTask");
    }

    /**
     * Вызывается при полном подключении игрока.
     */
    public void onConnection() {
        this.lastLoginTime = System.currentTimeMillis();
        this.lastLoginAddress = this.getEntity().getAddress().getAddress().getHostAddress();

        Database.getConnection().execute("UPDATE players SET LastLoginDate=?,LastLoginAddress=? WHERE PlayerId=?", this.lastLoginTime, this.lastLoginAddress, this.getObjectId());

        if (this.autoSaveTask != null) {
            return;
        }
        this.autoSaveTask = TaskMaster.scheduleGeneralAtFixedRate(new AutoSaveTask(), 0, 300000);
    }

    /**
     * Вызывается при полном отключении игрока.
     */
    public void onDisconnection() {
        this.lastLogoutTime = System.currentTimeMillis();
        Database.getConnection().execute("UPDATE players SET LastLogoutDate=? WHERE PlayerId=?", this.lastLogoutTime, this.getObjectId());

        if (this.autoSaveTask == null) {
            return;
        }

        this.autoSaveTask.cancel(true);
        this.autoSaveTask = null;
    }

    public boolean saveNow() {
        return true;
    }

    public void save() throws SQLException {
    }

    public void delete() throws SQLException {
    }

    /**
     * Загружает игрока из базы данных.
     *
     * @param row
     */
    public void loadFromDB(@NotNull DatabaseRow row) {
        this.setName(row.getString("Name"));

        this.gold = Math.max(0, row.getLong("Gold"));

        this.createdTime = row.getLong("CreatedDate");
        this.lastLoginTime = row.getLong("LastLoginDate");
        this.lastLogoutTime = row.getLong("LastLogoutDate");
        this.lastLoginAddress = row.getString("LastLoginAddress");
    }

    class AutoSaveTask implements Runnable {

        @Override
        public void run() {
            //FPlayer.this.safeSave();
        }

    }

    // ========================= Bukkit ========================
    public void sendMessage(@NotNull String message) {
        this.getEntity().sendMessage(message);
    }

    public void sendMessage(@NotNull String[] messages) {
        this.getEntity().sendMessage(messages);
    }

    public void sendMessage(@NotNull BaseComponent text) {
        this.getEntity().spigot().sendMessage(text);
    }

    // =========================== Command Sender ===========================
    public boolean isOp() {
        return this.getEntity().isOp();
    }

    public void setOp(boolean flag) {
        this.getEntity().setOp(flag);
    }

    public boolean isPermissionSet(String permission) {
        return this.getEntity().isPermissionSet(permission);
    }

    public boolean isPermissionSet(Permission permission) {
        return this.getEntity().isPermissionSet(permission);
    }

    public boolean hasPermission(String permission) {
        return this.getEntity().hasPermission(permission);
    }

    public boolean hasPermission(Permission permission) {
        return this.getEntity().hasPermission(permission);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean flag) {
        return this.getEntity().addAttachment(plugin, permission, flag);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.getEntity().addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean flag, int i) {
        return this.getEntity().addAttachment(plugin, permission, flag, i);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return this.getEntity().addAttachment(plugin, i);
    }

    public void removeAttachment(PermissionAttachment permission) {
        this.getEntity().removeAttachment(permission);
    }

    public void recalculatePermissions() {
        this.getEntity().recalculatePermissions();
    }

    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.getEntity().getEffectivePermissions();
    }

    public void sendMessage(@NotNull BaseComponent... message) {
        this.getEntity().spigot().sendMessage(message);
    }

    public Server getServer() {
        return this.getEntity().getServer();
    }

    public CommandSender.Spigot spigot() {
        return this.getEntity().spigot();
    }

    // =========================== Command Sender ===========================
    public void sendMessage(@NotNull SystemMessage message, Object... objects) {
        this.sendMessage(I18n.tl(message.getKey(), objects));
    }

    public void sendRawSuccess(@NotNull String message) {
        this.sendMessage(ChatColor.GREEN + message);
    }

    public void sendSuccess(@NotNull SystemMessage message, Object... objects) {
        this.sendMessage(ChatColor.GREEN + I18n.tl(message.getKey(), objects));
    }

    public void sendRawError(@NotNull String message) {
        this.sendMessage(ChatColor.RED + message);
    }

    public void sendError(@NotNull SystemMessage message, Object... objects) {
        this.sendMessage(ChatColor.RED + I18n.tl(message.getKey(), objects));
    }

    // Bukkit
    public void sendRawMessage(@NotNull String message) {
        this.getEntity().sendMessage(message);
    }

    public void sendRawMessage(@NotNull String[] messages) {
        this.getEntity().sendMessage(messages);
    }

}
