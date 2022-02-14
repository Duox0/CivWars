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
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.util.FileHelper;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class NpcTypes {

    private static NpcTypes instance;

    public static NpcTypes get() {
        return instance;
    }

    public static NpcTypes init() throws ContentLoadException {
        if (instance != null) {
            return instance;
        }

        instance = new NpcTypes();

        return instance;
    }

    private final Map<Integer, NPCTemplate> types = Maps.newHashMap();

    private NpcTypes() throws ContentLoadException {
        this.load();
    }

    private void load() throws ContentLoadException {
        this.types.clear();

        File folder = new File(CivWars.get().getDataFolder(), "data/npc_templates");
        List<File> files = FileHelper.files(folder, true);

        int highestId = 0;
        for (File file : files) {
            try {
                JsonObject object = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonObject();
                NPCTemplate type = Utilities.GSON.fromJson(object, NPCTemplate.class);
                Validate.isTrue(type.getId() > 0, "NPC Type ID must be greated than 0, got: " + type.getId());
                Validate.isTrue(!this.types.containsKey(type.getId()), "NPC Type with the ID " + type.getId() + " already exists");
                this.types.put(type.getId(), type);
                if (type.getId() > highestId) {
                    highestId = type.getId();
                }
            } catch (Throwable thrwbl) {
                throw new ContentLoadException(file.getPath(), thrwbl);
            }
        }

        CivLogger.log(Level.INFO, "Loaded {0} NPC Types", this.types.size());
        CivLogger.log(Level.INFO, "Highest NPC type id: {0}", highestId);
    }

    /**
     * @param id идентификатор NPCType.
     * @return NPCType или {@code null}, если NPCType с данным идентификатором
     * не найден.
     */
    @Nullable
    public static NPCTemplate get(int id) {
        return NpcTypes.instance.types.get(id);
    }

    /**
     * @param name имя NPCType.
     * @return NPCType или {@code null}, если NPCType с данным именем не найден.
     */
    @Nullable
    public static NPCTemplate get(@NotNull String name) {
        return NpcTypes.instance.types.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst().orElseGet(null);
    }

    /**
     *
     * @return список всех NPCType.
     */
    @NotNull
    public static Collection<NPCTemplate> values() {
        return NpcTypes.instance.types.values();
    }

}
