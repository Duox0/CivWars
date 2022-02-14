package ru.civwars.util.weighted;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import ru.lib27.annotation.NotNull;

public class WeightedSpawnerEntity extends WeightedRandomItem {

    private final NBTTagCompound nbt;

    public WeightedSpawnerEntity(@NotNull NBTTagCompound nbt, int weight) {
        super(weight);
        this.nbt = nbt;
    }

    public WeightedSpawnerEntity() {
        super(1);
        (this.nbt = new NBTTagCompound()).setString("id", "minecraft:pig");
    }

    @NotNull
    public NBTTagCompound getNbt() {
        return this.nbt;
    }

    public static class Serializer extends WeightedRandomItem.Serializer<WeightedSpawnerEntity> {

        public Serializer() {
            super("entity", WeightedSpawnerEntity.class);
        }

        @NotNull
        @Override
        public JsonElement serialize(@NotNull WeightedSpawnerEntity item, @NotNull JsonSerializationContext context) {
            return new JsonObject();
        }

        @NotNull
        @Override
        public WeightedSpawnerEntity deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new WeightedSpawnerEntity();
        }
    }
}
