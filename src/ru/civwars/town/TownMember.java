package ru.civwars.town;

import java.util.UUID;
import ru.civwars.civ.CivDefaultRanks;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.database.Database;
import ru.databaseapi.database.DatabaseRow;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class TownMember {

    /* Город, в котором находится этот участник. */
    private final Town town;

    /* Идентификатор игрока. */
    private final UUID id;

    /* Имя игрока. */
    private String name;

    /* Ранг игрока в городе. */
    private int rankId;

    /* Ранг игрока в цивилизации. */
    private int civRankId;

    /* Экземпляр игрока. */
    private KPlayer player = null;

    public TownMember(@NotNull Town town, @NotNull UUID id) {
        this.town = town;

        this.id = id;
    }

    public TownMember(@NotNull Town town, @NotNull KPlayer player, int rankId, int civRankId) {
        this.town = town;

        this.id = player.getObjectId();
        this.name = player.getName();

        this.rankId = rankId;
        this.civRankId = civRankId;

        this.player = player;
    }

    /**
     * Получает город, в котором находится этот участник.
     *
     * @return город.
     */
    @NotNull
    public final Town getTown() {
        return this.town;
    }

    /**
     * Идентификатор игрока.
     *
     * @return идентификатор игрока.
     */
    @NotNull
    public final UUID getId() {
        return this.id;
    }

    /**
     * Имя игрока.
     *
     * @return имя игрока.
     */
    @NotNull
    public final String getName() {
        return this.player != null ? this.player.getName() : this.name;
    }

    /**
     * Изменяет ранг игрока в городе.
     *
     * @param rankId ранг игрока.
     */
    public void changeRank(int rankId) {
        this.rankId = rankId;
        Database.getConnection().asyncExecute("UPDATE town_members SET Rank=? WHERE PlayerId=?", rankId, this.getId());
    }

    /**
     * Получает ранг игрока в городе.
     *
     * @return ранг игрока.
     */
    public final int getRankId() {
        return this.rankId;
    }

    /**
     * Изменяет ранг игрока в цивилизации.
     *
     * @param rankId ранг игрока.
     */
    public void changeCivRank(int rankId) {
        this.civRankId = rankId;
        Database.getConnection().asyncExecute("UPDATE town_members SET CivRank=? WHERE PlayerId=?", this.civRankId, this.getId());
    }

    /**
     * Получает ранг игрока в цивилизации.
     *
     * @return ранг игрока.
     */
    public final int getCivRankId() {
        return this.civRankId;
    }

    /**
     * Определяет, является ли цивилизационный ранг игрока выше указанного
     * ранга.
     *
     * @param rank
     * @return {@code true}, если цивилизационный ранг игрока выше указанного
     * ранга. Иначе {@code false}.
     */
    public final boolean isRankNotLower(@NotNull CivDefaultRanks rank) {
        return this.civRankId <= rank.getId();
    }

    public boolean isOnline() {
        return (this.player != null && this.player.isOnline());
    }

    @Nullable
    public final KPlayer getPlayer() {
        return this.player;
    }

    public void onConnection(@NotNull KPlayer player) {
        if (this.player != null) {
            return;
        }

        this.player = player;
    }

    public void onDisconnection() {
        if (this.player == null) {
            return;
        }

        this.name = this.player.getName();

        this.player = null;
    }

    public boolean loadFromDB(@NotNull DatabaseRow row) {
        this.name = row.getString("Name");

        this.rankId = row.getInt("Rank");
        this.civRankId = row.getInt("CivRank");

        return true;
    }
}
