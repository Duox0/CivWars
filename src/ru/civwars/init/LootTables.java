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
import ru.civwars.loot.LootTable;
import ru.civwars.CivWars;
import ru.civwars.exception.ContentLoadException;
import ru.civwars.util.FileHelper;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class LootTables {

    private static LootTables instance;

    public static LootTables get() {
        return instance;
    }

    public static LootTables init() throws ContentLoadException {
        if (instance != null) {
            return instance;
        }

        instance = new LootTables();

        return instance;
    }

    private final Map<String, LootTable> tables = Maps.newHashMap();

    private LootTables() throws ContentLoadException {
        this.load();
    }

    private void load() throws ContentLoadException {
        this.tables.clear();

        File folder = new File(CivWars.get().getDataFolder(), "data/loot_tables");
        List<File> files = FileHelper.files(folder, true);

        for (File file : files) {
            try {
                JsonObject object = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonObject();
                LootTable table = Utilities.GSON.fromJson(object, LootTable.class);
                String name = JsonUtils.getString(object, "name");
                Validate.isTrue(!this.tables.containsKey(name), "Loottable with the ID " + name + " already exists");
                this.tables.put(name, table);
            } catch (Throwable thrwbl) {
                throw new ContentLoadException(file.getPath(), thrwbl);
            }
        }

        CivLogger.log(Level.INFO, "Loaded {0} Loot Tables", this.tables.size());
    }

    /**
     * @param name имя таблицы.
     * @return таблица дропа или {@code null}, если таблица с данным именем не
     * найдена.
     */
    @Nullable
    public static LootTable get(@NotNull String name) {
        if (!LootTables.instance.tables.containsKey(name)) {
            return LootTable.EMPTY_LOOT_TABLE;
        }
        return LootTables.instance.tables.get(name);
    }

    /**
     *
     * @return список всех таблиц дропа.
     */
    @NotNull
    public static Collection<LootTable> values() {
        return LootTables.instance.tables.values();
    }

}
