package ru.civwars.petitions;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import ru.civwars.CivWars;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PetitionManager {

    private static CivWars plugin;

    private static final ConcurrentHashMap<UUID, Petition> petitions = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if(PetitionManager.plugin != null) {
            return;
        }
        PetitionManager.plugin = plugin;
    }

    private PetitionManager() {
    }

    /**
     * Добавляет петицию.
     *
     * @param petition петиция.
     */
    public static final void addPetition(@NotNull Petition petition) {
        petitions.put(petition.getId(), petition);
    }

    /**
     * Удаляет петицию.
     *
     * @param petition петиция.
     */
    public static final void removePetition(@NotNull Petition petition) {
        petitions.remove(petition.getId());
    }

    /**
     * @param petitionId идетификатор петиции.
     * @return петиция или {@code null}, если не найдена.
     */
    @Nullable
    public static Petition getPetition(@NotNull UUID petitionId) {
        return petitions.get(petitionId);
    }

    /**
     * @return список всех петиций.
     */
    @NotNull
    public static Collection<Petition> getPetition() {
        return petitions.values();
    }

}
