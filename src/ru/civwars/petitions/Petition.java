package ru.civwars.petitions;

import java.util.UUID;
import ru.lib27.annotation.NotNull;

public abstract class Petition {

    private final UUID id;

    public Petition(@NotNull UUID id) {
        this.id = id;
    }

    /**
     * @return идентификатор петиции.
     */
    @NotNull
    public final UUID getId() {
        return this.id;
    }

}
