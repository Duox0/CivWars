package ru.civwars.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import ru.civwars.building.types.BuildingData;
import ru.civwars.adapters.BuildingDataTypeAdapter;
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.item.CustomItem;
import ru.civwars.item.recipe.CustomRecipe;
import ru.civwars.loot.LootEntry;
import ru.civwars.loot.LootPool;
import ru.civwars.loot.LootTable;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.adapters.*;
import ru.civwars.loot.functions.LootFunction;
import ru.civwars.stat.functions.StatFunction;
import ru.civwars.util.item.RequiredItem;
import ru.civwars.util.number.RandomDouble;
import ru.civwars.util.number.RandomInt;
import ru.lib27.annotation.NotNull;

public class Utilities {

    public static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(RandomInt.class, new RandomIntTypeAdapter())
            .registerTypeAdapter(RandomDouble.class, new RandomDoubleTypeAdapter())
            .registerTypeAdapter(Material.class, new MaterialTypeAdapter())
            .registerTypeAdapter(RequiredItem.class, new RequiredItemTypeAdapter())
            .registerTypeAdapter(LootEntry.class, new LootEntryTypeAdapter())
            .registerTypeAdapter(LootCondition.class, new LootConditionTypeAdapter())
            .registerTypeAdapter(LootFunction.class, new LootFunctionTypeAdapter())
            .registerTypeAdapter(LootPool.class, new LootPoolTypeAdapter())
            .registerTypeAdapter(LootTable.class, new LootTableTypeAdapter())
            .registerTypeAdapter(StatFunction.class, new StatFunctionTypeAdapter())
            .registerTypeAdapter(CustomItem.class, new ItemTypeAdapter())
            .registerTypeAdapter(CustomRecipe.class, new RecipeTypeAdapter())
            .registerTypeAdapter(NPCTemplate.class, new NpcDataTypeAdapter())
            .registerTypeAdapter(BuildingData.class, new BuildingDataTypeAdapter())
            .create();

    private Utilities() {
    }

    public static int asInt(@NotNull String string, int value) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return value;
        }
    }

    @NotNull
    public static UUID uuidFromString(@NotNull String string) {
        return UUID.fromString(string);
    }

    /**
     * Возвращает новый массив аргументов, который начинается с аргумента,
     * заданного индексом, и продолжается до конца данного массива.
     *
     * @param args - аргументы.
     * @param firstIndex - начальный индекс, включительно.
     * @return
     */
    @NotNull
    public static String[] subargs(String[] args, int firstIndex) {
        Validate.notNull(args);

        if ((args.length - firstIndex) <= 0) {
            return new String[0];
        }

        String[] out = new String[args.length - firstIndex];
        for (int j = 0, length = out.length; j < length; j++) {
            out[j] = args[j + firstIndex];
        }
        return out;
    }

    public static boolean onlyLatin(@NotNull String name) {
        return (StringUtils.isAlpha(name) && StringUtils.isAsciiPrintable(name));
    }
}
