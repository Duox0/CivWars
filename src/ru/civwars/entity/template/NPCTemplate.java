package ru.civwars.entity.template;

import ru.civwars.bukkit.entity.BukkitEntityType;
import ru.lib27.annotation.NotNull;

public class NPCTemplate extends BasicEntityTemplate {

    /* BukkitEntityType. */
    private final BukkitEntityType entityType;

    private final double baseFollowRange;

    public NPCTemplate(@NotNull Property property) {
        super(property);
        this.entityType = property.entityType;

        this.baseFollowRange = property.baseFollowRange;
    }

    /**
     * Получает BukkitEntityType.
     *
     * @return
     */
    @NotNull
    public final BukkitEntityType getEntityType() {
        return this.entityType;
    }

    public final double getBaseFollowRange() {
        return this.baseFollowRange;
    }

    public static class Property extends BasicEntityTemplate.Property {

        private final BukkitEntityType entityType;

        private double baseFollowRange = 16.0D;

        public Property(int id, @NotNull String name, @NotNull BukkitEntityType entityType) {
            super(id, name);
            this.entityType = entityType;
        }

        public Property baseFollowRange(double baseFollowRange) {
            this.baseFollowRange = baseFollowRange;
            return this;
        }

    }
}
