package ru.civwars.entity;

import org.bukkit.entity.Entity;
import ru.civwars.entity.template.NPCTemplate;
import ru.lib27.annotation.NotNull;

public class MobEntity extends NPCEntity {

    private final NPCTemplate template;

    public MobEntity(@NotNull Entity entity, @NotNull NPCTemplate template) {
        super(entity);
        this.template = template;
    }

    @NotNull
    @Override
    public final NPCTemplate getTemplate() {
        return this.template;
    }
}
