package ru.civwars.init;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import ru.civwars.CivLogger;
import ru.civwars.CivWars;
import ru.civwars.exception.ContentLoadException;
import ru.civwars.building.types.BuildingData;
import ru.civwars.util.FileHelper;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BuildingTypes {

    private static CivWars plugin;
    
    private static final Map<Integer, BuildingData> buildings = Maps.newHashMap();
    private static final Map<String, BuildingData> buildingsByName = Maps.newHashMap();

    public static void init(@NotNull CivWars plugin)throws ContentLoadException {
        if (BuildingTypes.plugin != null) {
            return;
        }
        BuildingTypes.plugin = plugin;

        BuildingTypes.load();
    }

    private BuildingTypes() {
    }

    private static void load() throws ContentLoadException {
        File folder = new File(CivWars.get().getDataFolder(), "data/buildings");
        List<File> files = FileHelper.files(folder, true);

        int highestId = 0;
        for (File file : files) {
            try {
                JsonObject object = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonObject();
                BuildingData type = Utilities.GSON.fromJson(object, BuildingData.class);
                Validate.isTrue(type.getId() > 0, "Building ID must be greated than 0, got: " + type.getId());
                Validate.isTrue(!BuildingTypes.buildings.containsKey(type.getId()), "Building with the ID " + type.getId() + " already exists");
                Validate.isTrue(!BuildingTypes.buildingsByName.containsKey(type.getName().toLowerCase()), "Building with the Name " + type.getName()+ " already exists");
                BuildingTypes.buildings.put(type.getId(), type);
                BuildingTypes.buildingsByName.put(type.getName().toLowerCase(), type);
                if (type.getId() > highestId) {
                    highestId = type.getId();
                }
            } catch (Throwable thrwbl) {
                throw new ContentLoadException(file.getPath(), thrwbl);
            }
        }

        CivLogger.log(Level.INFO, "Loaded {0} Building Types", BuildingTypes.buildings.size());
        CivLogger.log(Level.INFO, "Highest building type id: {0}", highestId);
    }
    
    /**
     * Получает тип структуры по идентификатору.
     * @param id идентификатор типа структуры.
     * @return тип структуры или {@code null}, если тип с данным идентификатором
     * не найден.
     */
    @Nullable
    public static BuildingData get(int id) {
        return id > 0 ? BuildingTypes.buildings.get(id) : null;
    }

    /**
     * Получет тип структуры по имени.
     * @param name имя тип структуры.
     * @return тип структуры или {@code null}, если тип с данным именем не найден.
     */
    @Nullable
    public static BuildingData get(@NotNull String name) {
        return BuildingTypes.buildingsByName.get(name.toLowerCase());
    }

    /**
     * Получает список всех типов структур.
     * @return список типов структур.
     */
    @NotNull
    public static Collection<BuildingData> values() {
        return BuildingTypes.buildings.values();
    }
}
