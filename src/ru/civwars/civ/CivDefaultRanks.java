package ru.civwars.civ;

import ru.lib27.annotation.NotNull;

public enum CivDefaultRanks {
    MASTER(0),
    OFFICER(1),
    MEMBER(2);

    /* Идентификатор/уровень ранга. */
    private final int id;

    private CivDefaultRanks(int id) {
        this.id = id;
    }

    /**
     * Получает идентификатор ранга.
     *
     * @return идентификатор ранга.
     */
    public final int getId() {
        return this.id;
    }
    
    /**
     * Получает ранг по идентификатору.
     *
     * @param id идентификатор ранга.
     * @return ранг.
     */
    @NotNull
    public static CivDefaultRanks getRank(int id) {
        for (CivDefaultRanks rank : values()) {
            if (rank.id == id) {
                return rank;
            }
        }
        return CivDefaultRanks.MEMBER;
    }

}
