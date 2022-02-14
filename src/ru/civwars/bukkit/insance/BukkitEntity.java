package ru.civwars.bukkit.insance;

import org.bukkit.entity.Entity;
import ru.lib27.annotation.NotNull;

public class BukkitEntity {
    
    private final Entity entity;
    
    public BukkitEntity(@NotNull Entity entity) {
        this.entity = entity;
    }
    
    @NotNull
    public Entity getBukkitEntity() {
        return this.entity;
    }
    
}
