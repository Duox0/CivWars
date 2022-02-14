package ru.civwars.building.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.UUID;
import ru.civwars.CivLogger;
import ru.civwars.Config;
import ru.civwars.building.Building;
import ru.civwars.util.EnumFacing;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BuildingData {

    private final int id;
    private final String name;

    private final String markerIcon;

    public BuildingData(@NotNull Property property) {
        this.id = property.id;
        this.name = property.name;

        this.markerIcon = property.markerIcon;
    }

    /**
     *
     * @return главный идентификатор типа структуры.
     */
    public final int getId() {
        return this.id;
    }

    /**
     *
     * @return имя типа структуры.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * Получает имя иконки для отображения на веб-карте.
     *
     * @param building
     * @return имя иконка.
     */
    @NotNull
    public String getMarkerIcon(@Nullable Building building) {
        return this.markerIcon;
    }

    /**
     * @param building
     * @return отображаемое имя.
     */
    @NotNull
    public String getDisplayName(@Nullable Building building) {
        return this.name;
    }

    /**
     * Получает описание структуры для отображения на веб-карте.
     *
     * @param building
     * @return описание структуры.
     */
    @Nullable
    public String getMarkerDescription(@Nullable Building building) {
        return null;
    }

    @NotNull
    public final String getSchematicPath(@NotNull String theme, @NotNull EnumFacing facing) {
        StringBuilder builder = new StringBuilder();
        builder.append("templates/themes/").append(theme).append("/");
        builder.append("structures/").append(this.getName()).append("/");
        builder.append(this.getName()).append("_").append(facing.getName()).append(".def");

        String schematichPath = builder.toString().toLowerCase();
        if (Config.DEBUG) {
            CivLogger.info("Get schematic path: " + schematichPath);
        }
        return schematichPath;
    }

    @NotNull
    public abstract Building createBuilding(@NotNull UUID id, @NotNull CivWorld world);

    public static class Property {

        private final int id;
        private final String name;

        private String markerIcon = "null";

        public Property(int id, @NotNull String name) {
            this.id = id;
            this.name = name;
        }

        public Property markerIcon(@NotNull String markerIcon) {
            this.markerIcon = markerIcon;
            return this;
        }
    }
    
    public abstract static class Serializer<T extends BuildingData> {

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
        public final Class<T> getBuildingClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull Property property);
    }

}
