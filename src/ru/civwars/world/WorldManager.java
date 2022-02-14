package ru.civwars.world;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.civwars.CivWars;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class WorldManager {

    private static CivWars plugin;

    private static final ConcurrentHashMap<UUID, CivWorld> worlds = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if (WorldManager.plugin != null) {
            return;
        }
        WorldManager.plugin = plugin;
    }

    private WorldManager() {
    }

    /**
     * Добавляет мир.
     *
     * @param world мир.
     */
    public static void addWorld(@NotNull CivWorld world) {
        worlds.put(world.getId(), world);
    }

    /**
     * Удаляет мир.
     *
     * @param world мир.
     */
    public static void removeWorld(@NotNull CivWorld world) {
        worlds.remove(world.getId());
    }

    /**
     * Получает мир по идентификатору.
     *
     * @param worldId идентификатор мира.
     * @return мир или {@code null}, если мир с данным идентификатором не
     * найден.
     */
    @Nullable
    public static CivWorld getWorld(@NotNull UUID worldId) {
        return worlds.get(worldId);
    }

    /**
     * Получает мир по имени.
     *
     * @param name имя мира.
     * @return мир или {@code null}, если мир с данным именем не найден.
     */
    @Nullable
    public static CivWorld getWorld(@NotNull String name) {
        for (CivWorld world : worlds.values()) {
            if (world.getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        return null;
    }

    /**
     * Получает мир по Bukkit-объекту мира.
     *
     * @param world Bukkit-объект мира.
     * @return мир.
     */
    @NotNull
    public static CivWorld getWorld(@NotNull World world) {
        return worlds.get(world.getUID());
    }

    /**
     * Получает список всех миров.
     *
     * @return список миров.
     */
    @NotNull
    public static Collection<CivWorld> getWorlds() {
        return worlds.values();
    }

    /**
     * Загружает миры из хранилища данных.
     */
    public static void load() {
        WorldManager.worlds.clear();
        for (World bukkitWorld : Bukkit.getWorlds()) {
            CivWorld world = new CivWorld(bukkitWorld);
            WorldManager.addWorld(world);
        }

        for (CivWorld world : WorldManager.getWorlds()) {
            world.load();
        }
    }

    public static void completeSetup() {
        for (CivWorld world : WorldManager.getWorlds()) {
            world.completeSetup();
        }
    }

}
