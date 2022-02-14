package ru.civwars.town;

import ru.civwars.database.Database;
import ru.lib27.annotation.NotNull;

public class TownRank {

    /* Город, которому принадлежит этот ранг. */
    private final Town town;

    /* Идентификатор ранга. */
    private final int id;

    /* Имя ранга. */
    private final String name;
    
    /* Права ранга. */
    private final long rights;

    public TownRank(@NotNull Town town, int id, @NotNull String name, long rights) {
        this.town = town;

        this.id = id;
        this.name = name;
        this.rights = rights;
    }

    /**
     * Получает город, которому принадлежит этот ранг.
     *
     * @return город.
     */
    @NotNull
    public final Town getTown() {
        return this.town;
    }

    /**
     * Идентификатор ранга.
     *
     * @return идентификатор ранга.
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Имя ранга.
     *
     * @return имя ранга.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }
    
    /**
     * Получает права ранга.
     * @return права ранга.
     */
    public final long getRights() {
        return this.rights;
    }

    public void saveToDB() {
        Database.getConnection().execute("INSERT INTO town_ranks (TownId,RankId,Name,Rights) VALUES(?,?,?,?)",
                this.town.getId(),
                this.getId(),
                this.getName(),
                this.getRights()
        );
    }
    
}
