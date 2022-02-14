package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class BukkitSlime extends BukkitEntityType {

    private final int size;

    public BukkitSlime(int size) {
        super();
        this.size = size;
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location) {
        Slime entity = (Slime) this.spawnEntity(location, EntityType.SLIME);
        if (this.size > 0) {
            entity.setSize(this.size);
        }
        return entity;
    }

    public static class Serializer extends BukkitEntityType.Serializer<BukkitSlime> {

        protected Serializer() {
            super("slime", BukkitSlime.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull BukkitSlime item, @NotNull JsonSerializationContext context) {
            if (item.size > 0) {
                object.addProperty("size", item.size);
            }
        }

        @NotNull
        @Override
        public BukkitSlime deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            int size = JsonUtils.getIntOrDefault(object, "size", 0);
            return new BukkitSlime(size);
        }
    }
}
