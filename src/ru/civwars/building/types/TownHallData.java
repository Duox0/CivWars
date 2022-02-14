package ru.civwars.building.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.UUID;
import ru.civwars.building.instance.TownHall;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class TownHallData extends StructureData {

    public TownHallData(@NotNull BuildingData.Property property) {
        super(property);
    }
    
    @NotNull
    @Override
    public TownHall createBuilding(@NotNull UUID id, @NotNull CivWorld world) {
        return new TownHall(id, this, world);
    }

    public static class Serializer extends BuildingData.Serializer<TownHallData> {

        public Serializer() {
            super("town_hall", TownHallData.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull TownHallData item, @NotNull JsonSerializationContext context) {

        }

        @NotNull
        @Override
        public TownHallData deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull BuildingData.Property property) {
            return new TownHallData(property);
        }
    }
}
