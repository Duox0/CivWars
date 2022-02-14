package ru.civwars.building.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.UUID;
import ru.civwars.building.instance.Capitol;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class CapitolData extends TownHallData {

    public CapitolData(@NotNull BuildingData.Property property) {
        super(property);
    }
    
    @NotNull
    @Override
    public Capitol createBuilding(@NotNull UUID id, @NotNull CivWorld world) {
        return new Capitol(id, this, world);
    }
    
    public static class Serializer extends BuildingData.Serializer<CapitolData> {

        public Serializer() {
            super("capitol", CapitolData.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull CapitolData item, @NotNull JsonSerializationContext context) {
            
        }

        @NotNull
        @Override
        public CapitolData deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull StructureData.Property property) {
            return new CapitolData(property);
        }
    }
}
