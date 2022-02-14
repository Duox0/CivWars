package ru.civwars.loot;

import java.util.Random;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class LootContext {

    private final Random random;

    private final double luck;
    
    private final LivingEntity entity;
    private final Player player;

    public LootContext(@NotNull Random random, double luck, @Nullable LivingEntity entity, @Nullable Player player) {
        this.random = random;
        this.luck = luck;
        
        this.entity= entity;
        this.player = player;
    }

    @NotNull
    public Random getRandom() {
        return this.random;
    }

    @NotNull
    public double getLuck() {
        return this.luck;
    }
    
    @Nullable
    public LivingEntity getEntity() {
        return this.entity;
    }
    
    @Nullable
    public Player getKillerPlayer() {
        return this.player;
    }
    
    @Nullable
    public LivingEntity getKiller() {
        return this.entity != null ? this.entity.getKiller() : null;
    }

    @Nullable
    public LivingEntity getEntity(@NotNull LootContext.EntityTarget target) {
        switch(target) {
            case THIS:
                return this.getEntity();
            case KILLER:
                return this.getKiller();
            case KILLER_PLAYER:
                return this.getKillerPlayer();
        }
        return null;
    }

    public enum EntityTarget {
        THIS("this"),
        KILLER("killer"),
        KILLER_PLAYER("killer_player");

        private final String name;

        private EntityTarget(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public static EntityTarget getEntityTarget(@NotNull String name) {
            for (EntityTarget target : values()) {
                if (target.name.equals(name)) {
                    return target;
                }
            }
            throw new IllegalArgumentException("Invalid entity target " + name);
        }
    }
}
