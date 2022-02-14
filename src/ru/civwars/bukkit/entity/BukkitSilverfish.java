package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import ru.lib27.annotation.NotNull;

public class BukkitSilverfish extends BukkitEntityType {

    public BukkitSilverfish() {
        super();
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Silverfish entity = (Silverfish) this.spawnEntity(location, EntityType.SILVERFISH);
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitSilverfish> {

        protected Serializer() {
            super("silversifh", BukkitSilverfish.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitSilverfish item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public BukkitSilverfish deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new BukkitSilverfish();
        }
    }
}
