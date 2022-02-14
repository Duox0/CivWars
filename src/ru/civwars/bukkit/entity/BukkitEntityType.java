package ru.civwars.bukkit.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import ru.lib27.annotation.NotNull;

public abstract class BukkitEntityType {

    /**
     * Порождает существо в мире.
     * @param location
     * @param type
     * @return 
     */
    @NotNull
    protected final Entity spawnEntity(@NotNull Location location, @NotNull EntityType type) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        return entity;
    }
    
    /**
     * Порождает существо в мире.
     * @param location
     * @return 
     */
    @NotNull
    public abstract Entity spawn(@NotNull Location location);

    public abstract static class Serializer<T extends BukkitEntityType> {

        private final String name;
        private final Class<T> clazz;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getEntityTypeClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context);
    }
}
