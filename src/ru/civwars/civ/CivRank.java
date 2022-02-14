package ru.civwars.civ;

import ru.civwars.database.Database;
import ru.lib27.annotation.NotNull;

public class CivRank {

    /* Цивилизация, которой принадлежит этот ранг. */
    private final Civilization civ;

    /* Идентификатор ранга. */
    private final int id;

    /* Имя ранга. */
    private final String name;
    
    /* Права ранга. */
    private final long rights;

    public CivRank(@NotNull Civilization civ, int id, @NotNull String name, long rights) {
        this.civ = civ;

        this.id = id;
        this.name = name;
        this.rights = rights;
    }

    /**
     * Получает цивилизацию, которой принадлежит этот ранг.
     *
     * @return цивилизация.
     */
    @NotNull
    public final Civilization getCiv() {
        return this.civ;
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
        Database.getConnection().execute("INSERT INTO civilization_ranks (CivId,RankId,Name,Rights) VALUES(?,?,?,?)",
                this.civ.getId(),
                this.getId(),
                this.getName(),
                this.getRights()
        );
    }
    
}
