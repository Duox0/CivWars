package ru.civwars.object;

import java.util.UUID;
import org.bukkit.Location;
import ru.civwars.CivWars;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class GameObject {

    protected final CivWars civcraft = CivWars.get();
    
    /* Главный идентификатор объекта. */
    private final UUID id;

    /* Имя объекта. */
    private String name;

    public GameObject(@NotNull UUID id) {
        this.id = id;
    }

     /**
     * Получает идентификатор объекта.
     * @return идентификатор объекта.
     */
    @NotNull
    public final UUID getObjectId() {
        return this.id;
    }
    
    /**
     * Устанавливает имя объекта.
     * @param name имя.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }
    
    /**
     * Получает имя объекта.
     * @return имя объекта.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * Получает мир, в котором находится объект.
     * @return мир или {@code null}, если вне мира.
     */
    @Nullable
    public abstract CivWorld getWorld();
    
    /**
     * Получает расположение.
     * @return расположение или {@code null}, если вне мира.
     */
    @Nullable
    public abstract Location getLocation();
}
