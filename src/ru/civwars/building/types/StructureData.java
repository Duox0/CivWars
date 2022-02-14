package ru.civwars.building.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.UUID;
import ru.civwars.building.instance.Structure;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class StructureData extends BuildingData {

    public StructureData(@NotNull BuildingData.Property property) {
        super(property);
    }

    @NotNull
    @Override
    public Structure createBuilding(@NotNull UUID id, @NotNull CivWorld world) {
        return new Structure(id, this, world);
    }

    public static class Serializer extends BuildingData.Serializer<StructureData> {

        public Serializer() {
            super("structure", StructureData.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull StructureData item, @NotNull JsonSerializationContext context) {

        }

        @NotNull
        @Override
        public StructureData deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull BuildingData.Property property) {
            return new StructureData(property);
        }
    }
}
