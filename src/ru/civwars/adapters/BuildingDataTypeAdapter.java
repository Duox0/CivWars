package ru.civwars.adapters;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import ru.civwars.building.types.*;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class BuildingDataTypeAdapter implements JsonSerializer<BuildingData>, JsonDeserializer<BuildingData> {

    private static final Map<String, BuildingData.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends BuildingData>, BuildingData.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerType(new StructureData.Serializer());
        registerType(new CapitolData.Serializer());
        registerType(new TownHallData.Serializer());
    }

    public static <T extends BuildingData> void registerType(@NotNull BuildingData.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getBuildingClass();
        if (BuildingDataTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register building data type " + type);
        }
        if (BuildingDataTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register building data class " + clazz.getName());
        }
        BuildingDataTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        BuildingDataTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static BuildingData.Serializer<?> getSerializerForType(@NotNull String type) {
        BuildingData.Serializer<?> serializer = BuildingDataTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown building data type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends BuildingData> BuildingData.Serializer<T> getSerializerFor(@NotNull T function) {
        BuildingData.Serializer<T> serializer = (BuildingData.Serializer<T>) BuildingDataTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown building data " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(BuildingData data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        BuildingData.Serializer<BuildingData> serializer = BuildingDataTypeAdapter.getSerializerFor(data);
        object.addProperty("base_class", serializer.getName());

        object.addProperty("id", data.getId());
        object.addProperty("name", data.getName());

        object.addProperty("marker_icon", data.getMarkerIcon(null));

        serializer.serialize(object, data, context);

        return object;
    }

    @Override
    public BuildingData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "building data");

        String buildingType = JsonUtils.getString(object, "type");
        BuildingData.Serializer<?> serializer;
        try {
            serializer = BuildingDataTypeAdapter.getSerializerForType(buildingType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown structure data type '" + buildingType + "'");
        }

        int id = JsonUtils.getInt(object, "id");
        String name = JsonUtils.getString(object, "name");

        BuildingData.Property property = new BuildingData.Property(id, name);

        property.markerIcon(JsonUtils.getStringOrDefault(object, "marker_icon", "null"));

        return (BuildingData) serializer.deserialize(object, context, property);
    }
}
