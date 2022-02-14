package ru.civwars;

import ru.civwars.i18n.I18n;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.civwars.building.Building;
import ru.civwars.commands.CommandCivCraft;
import ru.civwars.listeners.BlockListener;
import ru.civwars.listeners.ChunkListener;
import ru.civwars.listeners.ConnectionListener;
import ru.civwars.building.BuildingManager;
import ru.civwars.building.instance.Structure;
import ru.civwars.building.validation.StructureValidator;
import ru.civwars.civ.CivManager;
import ru.civwars.commands.CommandBalance;
import ru.civwars.commands.CommandGive;
import ru.civwars.commands.CommandShow;
import ru.civwars.commands.admin.CommandAdmin;
import ru.civwars.commands.mobs.CommandMobs;
import ru.civwars.commands.test.CommandTest;
import ru.civwars.commands.town.CommandTown;
import ru.civwars.exception.ContentLoadException;
import ru.civwars.init.CustomItems;
import ru.civwars.init.LootTables;
import ru.civwars.init.NpcTypes;
import ru.civwars.listeners.EntityListener;
import ru.civwars.listeners.InventoryListener;
import ru.civwars.listeners.PlayerListener;
import ru.civwars.listeners.WorldListener;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.commands.civ.CommandCiv;
import ru.civwars.database.Database;
import ru.civwars.entity.player.PlayerCache;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.init.BuildingTypes;
import ru.civwars.init.CustomRecipes;
import ru.civwars.menu.MenuManager;
import ru.civwars.petitions.PetitionManager;
import ru.civwars.task.CultureProcessTask;
import ru.civwars.thread.sync.BuildUpdateSyncTask;
import ru.civwars.thread.sync.ChunkSyncTask;
import ru.civwars.town.TownManager;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.Utilities;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class CivWars extends JavaPlugin implements Listener, Runnable {

    public static final String ID = "CivCraft";
    public static final String NAME = "Civilization Craft";

    private static CivWars instance;

    private ScheduledFuture<?> cultureProcessTask = null;

    public CivWars() {
    }

    @Override
    public void onEnable() {
        CivWars.instance = this;
        CivLogger.init(this);

        CivLogger.info("================================================");
        CivLogger.info("============ Server Initialize Start ===========");
        CivLogger.info("================================================");

        Config.load();
        Database.init(this);

        new I18n(this).onEnable();
        I18n.getInstance().updateLocale("ru");

        if(!this.initContent()) {
            return;
        }
        
        TaskMaster.init(this);
        CultureProcessTask.init();

        BuildUpdateSyncTask.init(this);
        ChunkSyncTask.init(this);
        
        this.initGlobals();
        this.loadGlobals();

        this.initListeners();
        this.initCommands();

        TaskMaster.runTask(() -> {
            this.startGame();
        });
    }

    @Override
    public void onDisable() {

        if (this.cultureProcessTask != null) {
            this.cultureProcessTask.cancel(true);
            this.cultureProcessTask = null;
        }
    }

    private void createTables() {
    }

    /**
     * Загружает весь игровой контент (предметы, рецепты, структуры, мобов и
     * т.п.)
     *
     * @return {@code true} при успешном выполнении. Иначе {@code false}.
     */
    private boolean initContent() {
        CivLogger.info("================================================");
        CivLogger.info("=========== Content Initialize Start ===========");
        CivLogger.info("================================================");

        try {
            BlockUtils.init(this);
            BlockUtils.instance().load();
            
            NpcTypes.init();
            
            CustomItems.init();
            CustomRecipes.init();
            BuildingTypes.init(this);
            
            LootTables.init();
            
            SchematicManager.init(this);
        } catch (ContentLoadException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
            return true;
        }
        return true;
    }
    
    private void initGlobals() {
        CivLogger.info("================================================");
        CivLogger.info("=========== Globals Initialize Start ===========");
        CivLogger.info("================================================");
        StructureValidator.init(this);
        
        MenuManager.init();

        PlayerCache.init(this);
        PlayerManager.init(this);
        PetitionManager.init(this);

        WorldManager.init(this);
        CivManager.init(this);
        TownManager.init(this);
        //PlotManager.init(this);
        BuildingManager.init(this);
    }
    
    private void loadGlobals() {
        CivLogger.info("================================================");
        CivLogger.info("============= Globals Loading Start ============");
        CivLogger.info("================================================");

        // load
        PlayerCache.load();
        WorldManager.load();
        CivManager.load();
        TownManager.load();
        //PlotManager.load();
        BuildingManager.load();

        // validate
        BuildingManager.validate();
        TownManager.validate();
        CivManager.validate();
        //PlotManager.validate();
    }
    
    private void initListeners() {
        CivLogger.info("================================================");
        CivLogger.info("=========== Listeners Initialize Start =========");
        CivLogger.info("================================================");
        final PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(this, this);

        ConnectionListener.init(this);
        WorldListener.init(this);
        ChunkListener.init(this);
        BlockListener.init(this);
        EntityListener.init(this);
        PlayerListener.init(this);
        InventoryListener.init(this);
    }
    
    private void initCommands() {
        CivLogger.info("================================================");
        CivLogger.info("=========== Commands Initialize Start ==========");
        CivLogger.info("================================================");
        this.getCommand("test").setExecutor(new CommandTest(this));

        this.getCommand("civcraft").setExecutor(new CommandCivCraft(this));
        this.getCommand("admin").setExecutor(new CommandAdmin(this));
        this.getCommand("give").setExecutor(new CommandGive(this));

        this.getCommand("show").setExecutor(new CommandShow(this));
        this.getCommand("balance").setExecutor(new CommandBalance(this));

        this.getCommand("civ").setExecutor(new CommandCiv(this));
        this.getCommand("town").setExecutor(new CommandTown(this));

        this.getCommand("mobs").setExecutor(new CommandMobs(this));
    }

    private void startGame() {
        
        WorldManager.completeSetup();

        TaskMaster.runTaskTimerAsynchronously(() -> {
            for (CivWorld world : WorldManager.getWorlds()) {
                for (Building building : world.getStructures()) {
                }
            }
        }, 0L, 20 * 60 * 5);

        // run sync tasks
        TaskMaster.runTaskTimer(BuildUpdateSyncTask.instance(), 0L, 1L);
        TaskMaster.runTaskTimer(ChunkSyncTask.instance(), 0L, 1L);

        this.cultureProcessTask = TaskMaster.scheduleGeneralAtFixedRate(CultureProcessTask.get(), 0L, 1000L);

        TaskMaster.runTaskTimer(CivWars.get(), 0, 1L);
        
        CivLogger.info("================================================");
        CivLogger.info("================== Server Run ==================");
        CivLogger.info("================================================");
    }
    
    private void stopGame() {
        CivManager.getCivs().forEach(f -> {
        });
    }
    
    @Override
    public void run() {
        for (CivWorld world : WorldManager.getWorlds()) {
            world.tick();
        }
    }

    @NotNull
    public static CivWars get() {
        return CivWars.instance;
    }
}
