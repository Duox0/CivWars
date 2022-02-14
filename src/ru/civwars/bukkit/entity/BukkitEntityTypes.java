package ru.civwars.bukkit.entity;

import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public enum BukkitEntityTypes {
    BABY_ZOMBIE("baby_zombie", new BukkitBabyZombie.Serializer()),
    CREEPER("creeper", new BukkitCreeper.Serializer()),
    SILVERFISH("silverfish", new BukkitSilverfish.Serializer()),
    SKELETON("skeleton", new BukkitSkeleton.Serializer()),
    SLIME("slime", new BukkitSlime.Serializer()),
    SPIDER("spider", new BukkitSpider.Serializer()),
    ZOMBIE("zombie", new BukkitZombie.Serializer());

    private final String name;
    private final BukkitEntityType.Serializer<?> serializer;

    private BukkitEntityTypes(@NotNull String name, @NotNull BukkitEntityType.Serializer<?> serializer) {
        this.name = name;
        this.serializer = serializer;
    }

    @NotNull
    public final BukkitEntityType.Serializer<?> getSerializer() {
        return this.serializer;
    }

    @Nullable
    public static BukkitEntityTypes getEntityType(int id) {
        if (id < 0 || id >= BukkitEntityTypes.values().length) {
            return null;
        }
        return BukkitEntityTypes.values()[id];
    }

    @Nullable
    public static BukkitEntityTypes getEntityType(@NotNull String name) {
        for (BukkitEntityTypes type : BukkitEntityTypes.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
