package ru.civwars.stat;

import ru.civwars.entity.NPCEntity;
import ru.lib27.annotation.NotNull;

public class NPCStat extends BasicStat {

    public NPCStat(@NotNull NPCEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public NPCEntity getEntity() {
        return (NPCEntity) super.getEntity();
    }

}
