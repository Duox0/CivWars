package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class BukkitCreeper extends BukkitEntityType {

    private final boolean powered;
    private final int maxFuseTicks;
    private final int explosionRadius;

    public BukkitCreeper(boolean powered, int maxFuseTicks, int explosionRadius) {
        super();
        this.powered = powered;
        this.maxFuseTicks = maxFuseTicks;
        this.explosionRadius = explosionRadius;
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Creeper entity = (Creeper) this.spawnEntity(location, EntityType.CREEPER);
        entity.setPowered(this.powered);
        if (this.maxFuseTicks >= 0) {
            entity.setMaxFuseTicks(this.maxFuseTicks);
        }
        if (this.explosionRadius >= 0) {
            entity.setExplosionRadius(this.explosionRadius);
        }
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitCreeper> {

        protected Serializer() {
            super("creeper", BukkitCreeper.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitCreeper item, @NotNull JsonSerializationContext context) {
            if (item.powered) {
                object.addProperty("powered", item.powered);
            }
            if (item.maxFuseTicks >= 0) {
                object.addProperty("max_fuse_ticks", item.maxFuseTicks);
            }
            if (item.explosionRadius >= 0) {
                object.addProperty("explosion_radius", item.explosionRadius);
            }
        }

        @NotNull
        @Override
        public BukkitCreeper deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            boolean powered = JsonUtils.getBooleanOrDefault(object, "powered", false);
            int maxFuseTicks = JsonUtils.getIntOrDefault(object, "max_fuse_ticks", -1);
            int explosionRadius = JsonUtils.getIntOrDefault(object, "explosion_radius", -1);
            return new BukkitCreeper(powered, maxFuseTicks, explosionRadius);
        }
    }
}
