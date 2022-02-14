package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import ru.lib27.annotation.NotNull;

public class BukkitSpider extends BukkitEntityType {

    public BukkitSpider() {
        super();
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Spider entity = (Spider) this.spawnEntity(location, EntityType.SPIDER);
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitSpider> {

        protected Serializer() {
            super("spider", BukkitSpider.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitSpider item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public BukkitSpider deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new BukkitSpider();
        }
    }
}
