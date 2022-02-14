package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import ru.lib27.annotation.NotNull;

public class BukkitSkeleton extends BukkitEntityType {

    public BukkitSkeleton() {
        super();
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Skeleton entity = (Skeleton) this.spawnEntity(location, EntityType.SKELETON);
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitSkeleton> {

        protected Serializer() {
            super("skeleton", BukkitSkeleton.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitSkeleton item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public BukkitSkeleton deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new BukkitSkeleton();
        }
    }
}
