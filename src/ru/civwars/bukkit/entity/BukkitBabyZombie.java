package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import ru.lib27.annotation.NotNull;

public class BukkitBabyZombie extends BukkitEntityType {

    public BukkitBabyZombie() {
        super();
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Zombie entity = (Zombie) this.spawnEntity(location, EntityType.ZOMBIE);
        entity.setBaby(true);
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitBabyZombie> {

        protected Serializer() {
            super("baby_zombie", BukkitBabyZombie.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitBabyZombie item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public BukkitBabyZombie deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new BukkitBabyZombie();
        }
    }
}
