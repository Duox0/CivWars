package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import ru.lib27.annotation.NotNull;

public class BukkitZombie extends BukkitEntityType {

    public BukkitZombie() {
        super();
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Zombie entity = (Zombie) this.spawnEntity(location, EntityType.ZOMBIE);
        entity.setBaby(false);
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitZombie> {

        protected Serializer() {
            super("zombie", BukkitZombie.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitZombie item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public BukkitZombie deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new BukkitZombie();
        }
    }
}
