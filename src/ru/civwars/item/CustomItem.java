package ru.civwars.item;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.stat.functions.StatFunction;
import ru.civwars.util.EnumInteractionResult;
import ru.civwars.util.InteractionResult;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CustomItem {

    public static final StatFunction[] EMPTY_FUNCTIONS = new StatFunction[0];

    /* Идентификатор предмета. */
    private final int id;

    /* Имя предмета. */
    private final String name;

    /* Материал предмета. */
    private final Material item;

    /* Прочность предмета. */
    private final short data;

    // Stats
    private final StatFunction[] statFunctions;

    public CustomItem(@NotNull Property property) {
        this.id = property.id;
        this.name = property.name;
        this.item = property.item;
        this.data = property.data;

        this.statFunctions = property.statFunctions;
    }

    /**
     * Получает идентификатор предмета.
     *
     * @return
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Получает имя предмета.
     *
     * @return
     */
    @NotNull
    public final String getName() {
        return this.name;
    }
    
    public final Material getItem() {
        return this.item;
    }
    
    public final int getMetadata() {
        return this.data;
    }

    @Nullable
    public BukkitItemSlot getEquipmentSlot() {
        return null;
    }

    @NotNull
    public StatFunction[] getStatFunctions(@NotNull ItemStack stack) {
        if (this.statFunctions == null) {
            return EMPTY_FUNCTIONS;
        }

        List<StatFunction> functions = Lists.newArrayList();
        for (StatFunction function : this.statFunctions) {
            functions.add(function.createInstance(stack));
        }

        if (functions.isEmpty()) {
            return EMPTY_FUNCTIONS;
        }
        return functions.toArray(new StatFunction[functions.size()]);
    }

    @NotNull
    public ItemStack createItemStack(int count, @Nullable KPlayer player) {
        Material item = this.item != Material.AIR ? this.item : Material.STICK;
        int fixCount = Math.max(1, Math.min(item.getMaxStackSize(), count));
        int fixData = this.data;
        if (item.getMaxDurability() > 0) {
            fixData = Math.max(0, Math.min(item.getMaxDurability(), this.data));
        }

        ItemStack stack = new ItemStack(item, fixCount, (short) fixData);

        NMSItemStack nmsStack = NMSItemStack.getNMSItemStack(stack, this);

        return nmsStack.build();
    }
    
    @NotNull
    public ItemStack createItemStack(int count) {
        return this.createItemStack(count, null);
    }

    @NotNull
    public ItemStack createItemStack() {
        return this.createItemStack(1);
    }

    protected void apply(@NotNull NMSItemStack stack) {
    }

    @NotNull
    public InteractionResult<ItemStack> useItemRightClick(@NotNull ItemStack stack, @NotNull KPlayer player) {
        return new InteractionResult<ItemStack>(EnumInteractionResult.PASS, stack);
    }

    @NotNull
    public InteractionResult<ItemStack> useItemRightClickBlock(@NotNull ItemStack stack, @NotNull KPlayer player, @NotNull Block block, @NotNull BlockFace facing) {
        return new InteractionResult<ItemStack>(EnumInteractionResult.PASS, stack);
    }

    public static class Property {

        private final int id;
        private final String name;
        private final Material item;
        private final short data;

        // Stats
        protected StatFunction[] statFunctions = EMPTY_FUNCTIONS;

        public Property(int id, @NotNull String name, @NotNull Material item, short data) {
            this.id = id;
            this.name = name;
            this.item = item;
            this.data = data;
        }

        public Property statFunctions(@NotNull StatFunction[] functions) {
            this.statFunctions = functions;
            return this;
        }

    }

    public abstract static class Serializer<T extends CustomItem> {

        private final String name;
        private final Class<T> clazz;

        public Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getConditionClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property);
    }

}
