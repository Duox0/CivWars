package ru.civwars.entity;

import org.bukkit.entity.Entity;
import ru.civwars.stat.BasicStat;
import ru.civwars.stat.NPCStat;
import ru.lib27.annotation.NotNull;

public class NPCEntity extends BasicEntity<Entity> {
    
    public NPCEntity(@NotNull Entity entity) {
        super(entity);
    }
    
    @NotNull
    @Override
    protected BasicStat createStat() {
        return new NPCStat(this);
    }
    
}
