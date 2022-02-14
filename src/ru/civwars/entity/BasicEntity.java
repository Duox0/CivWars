package ru.civwars.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import ru.civwars.CivWars;
import ru.civwars.entity.template.BasicEntityTemplate;
import ru.civwars.object.GameObject;
import ru.civwars.stat.BasicStat;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BasicEntity<T extends Entity> extends GameObject {

    private final T entity;

    private final BasicStat stat;

    private boolean isDead = false;

    public BasicEntity(@NotNull T entity) {
        super(entity.getUniqueId());
        this.setName(entity.getName());
        this.entity = entity;

        this.stat = this.createStat();
        this.initStats();
    }
    
    /**
     * Получает новый объект BasicStat.
     * @return BasicStat.
     */
    @NotNull
    protected BasicStat createStat() {
        return new BasicStat(this);
    }

    protected void initStats() {
    }
    
    public void init() {
    }
    
    @NotNull
    public T getEntity() {
        return this.entity;
    }

    @Nullable
    public BasicEntityTemplate getTemplate() {
        return null;
    }

    /**
     * Получает объект BasicStat.
     * @return BasicStat.
     */
    @NotNull
    public final BasicStat getStat() {
        return this.stat;
    }

    @NotNull
    @Override
    public CivWorld getWorld() {
        return WorldManager.getWorld(this.entity.getWorld());
    }

    @NotNull
    @Override
    public Location getLocation() {
        return this.entity.getLocation();
    }

    public double getAttribute(@NotNull Attribute attribute, double baseValue) {
        if (this.entity instanceof LivingEntity) {
            LivingEntity asLiving = (LivingEntity) this.entity;
            AttributeInstance attr = asLiving.getAttribute(attribute);
            if (attr != null) {
                return attr.getValue();
            }
        }
        return baseValue;
    }

    public void setDead() {
        if (!this.isDead) {
            this.isDead = true;
            this.unregister();
        }
    }

    public boolean isDead() {
        return this.isDead;
    }

    public void tick() {
        if (this.entity.isDead()) {
            this.setDead();
            return;
        }
    }

    public void despawn() {
        this.unregister();
        if (this.entity != null) {
            this.entity.remove();
        }
    }

    public void unregister() {
        Bukkit.getScheduler().runTaskLater(CivWars.get(), () -> getWorld().unregisterEntity(this), 1L);
    }
}
