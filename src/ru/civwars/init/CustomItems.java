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
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivLogger;
import ru.civwars.CivWars;
import ru.civwars.exception.ContentLoadException;
import ru.civwars.item.CustomItem;
import ru.civwars.util.FileHelper;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CustomItems {

    private static CustomItems instance;

    public static CustomItems get() {
        return instance;
    }

    public static CustomItems init() throws ContentLoadException {
        if (instance != null) {
            return instance;
        }

        instance = new CustomItems();

        return instance;
    }

    private final Map<Integer, CustomItem> items = Maps.newHashMap();

    private CustomItems() throws ContentLoadException {
        this.load();
    }

    private void load() throws ContentLoadException {
        this.items.clear();

        File folder = new File(CivWars.get().getDataFolder(), "data/custom_items");
        List<File> files = FileHelper.files(folder, true);

        int highestId = 0;
        for (File file : files) {
            try {
                JsonObject object = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonObject();
                CustomItem item = Utilities.GSON.fromJson(object, CustomItem.class);
                Validate.isTrue(item.getId() > 0, "Item ID must be greated than 0, got: " + item.getId());
                Validate.isTrue(!this.items.containsKey(item.getId()), "Item with the ID " + item.getId() + " already exists");
                this.items.put(item.getId(), item);
                if (item.getId() > highestId) {
                    highestId = item.getId();
                }
            } catch (Throwable thrwbl) {
                throw new ContentLoadException(file.getPath(), thrwbl);
            }
        }

        CivLogger.log(Level.INFO, "Loaded {0} Custom Items", this.items.size());
        CivLogger.log(Level.INFO, "Highest item id: {0}", highestId);
    }

    /**
     * @param id идентификатор предмета.
     * @return предмет или {@code null}, если предмет с данным идентификатором
     * не найден.
     */
    @Nullable
    public static CustomItem get(int id) {
        return CustomItems.instance.items.get(id);
    }

    /**
     * @param name имя предмета.
     * @return предмет или {@code null}, если предмет с данным именем не найден.
     */
    @Nullable
    public static CustomItem get(@NotNull String name) {
        return CustomItems.instance.items.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst().orElseGet(null);
    }

    /**
     *
     * @return список всех предметов.
     */
    @NotNull
    public static Collection<CustomItem> values() {
        return CustomItems.instance.items.values();
    }

    /**
     * Получает CustomItem по предмету.
     *
     * @param stack предмет.
     * @return CustomItem или null, если предмет не является кастомным.
     */
    @Nullable
    public static CustomItem fromItemStack(ItemStack stack) {
        if (ItemUtils.isEmpty(stack)) {
            return null;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack.getTag() != null && nmsStack.getTag().hasKeyOfType("mid", 3)) {
            return CustomItems.get(nmsStack.getTag().getInt("mid"));
        }
        return null;
    }

}
