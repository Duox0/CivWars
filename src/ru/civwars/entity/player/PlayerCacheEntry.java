package ru.civwars.entity.player;

import java.util.UUID;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PlayerCacheEntry {

    /* Идентификатор игрока. */
    private final UUID id;

    /* Имя игрока. */
    private String name;

    /* Время последнего выхода с сервера. */
    private long lastLogoutTime = 0;

    private UUID townId = null;

    public PlayerCacheEntry(@NotNull UUID id, @NotNull String name) {
        this.id = id;
    }

    @NotNull
    public UUID getId() {
        return this.id;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setLastLogoutTime(long time) {
        this.lastLogoutTime = time;
    }

    public long getLastLogoutTime() {
        return this.lastLogoutTime;
    }

    public void setTownId(@Nullable UUID townId) {
        this.townId = townId;
    }

    @Nullable
    public UUID getTownId() {
        return this.townId;
    }
}
