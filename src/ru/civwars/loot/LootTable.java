package ru.civwars.loot;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.math.MathHelper;
import ru.lib27.annotation.NotNull;

public class LootTable {

    public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);
    
    private final LootPool[] pools;

    public LootTable(@NotNull LootPool[] pools) {
        this.pools = pools;
    }
    
    @NotNull
    public final LootPool[] getPools() {
        return this.pools;
    }

    @NotNull
    public List<ItemStack> generateLootForPools(@NotNull LootContext context) {
        ArrayList<ItemStack> stacks = Lists.newArrayList();
        LootPool[] pools = this.pools;
        for (LootPool pool : this.pools) {
            pool.generateLoot(stacks, context);
        }
        return stacks;
    }
    
    public void fillInventory(@NotNull Inventory inventory, @NotNull LootContext context) {
        List<ItemStack> stacks = this.generateLootForPools(context);
        List<Integer> emptySlots = this.getEmptySlotsRandomized(inventory, context.getRandom());
        this.shuffleItems(stacks, emptySlots.size(), context.getRandom());
        for (ItemStack stack : stacks) {
            if (emptySlots.isEmpty()) {
                Logger.getLogger(LootTable.class.getSimpleName()).warning("Tried to over-fill a container");
                return;
            }
            if (ItemUtils.isEmpty(stack)) {
                inventory.setItem(emptySlots.remove(emptySlots.size() - 1), null);
            } else {
                inventory.setItem(emptySlots.remove(emptySlots.size() - 1), stack);
            }
        }
    }

    private void shuffleItems(@NotNull List<ItemStack> stacks, int emptySlots, @NotNull Random random) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        Iterator<ItemStack> iterator = stacks.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (ItemUtils.isEmpty(stack)) {
                iterator.remove();
            } else {
                if (stack.getAmount() <= 1) {
                    continue;
                }
                list.add(stack);
                iterator.remove();
            }
        }
        emptySlots -= stacks.size();
        while (emptySlots > 0 && !list.isEmpty()) {
            ItemStack stack2 = list.remove(MathHelper.nextInt(random, 0, list.size() - 1));
            int i = MathHelper.nextInt(random, 1, stack2.getAmount() / 2);
            ItemStack stack3 = ItemUtils.splitStack(stack2, i);
            if (stack2.getAmount() > 1 && random.nextBoolean()) {
                list.add(stack2);
            } else {
                stacks.add(stack2);
            }
            if (stack3.getAmount() > 1 && random.nextBoolean()) {
                list.add(stack3);
            } else {
                stacks.add(stack3);
            }
        }
        stacks.addAll(list);
        Collections.shuffle(stacks, random);
    }

    private List<Integer> getEmptySlotsRandomized(@NotNull Inventory inventory, @NotNull Random random) {
        final ArrayList<Integer> list = Lists.newArrayList();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (ItemUtils.isEmpty(inventory.getItem(i))) {
                list.add(i);
            }
        }
        Collections.shuffle(list, random);
        return list;
    }

}
