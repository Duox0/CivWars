package ru.civwars.loot;

import ru.civwars.loot.conditions.LootCondition;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.math.MathHelper;
import ru.civwars.util.number.RandomInt;
import ru.lib27.annotation.NotNull;

public class LootPool {

    private final LootEntry[] entries;
    private final LootCondition[] conditions;
    private final RandomInt rolls;
    private final RandomInt bonusRolls;

    public LootPool(@NotNull LootEntry[] entries, @NotNull LootCondition[] conditions, @NotNull RandomInt rolls, @NotNull RandomInt bonusRolls) {
        this.entries = entries;
        this.conditions = conditions;
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
    }
    
    @NotNull
    public final LootEntry[] getEntries() {
        return this.entries;
    }
    
    @NotNull
    public final LootCondition[] getConditions() {
        return this.conditions;
    }
    
    @NotNull
    public final RandomInt getRolls() {
        return this.rolls;
    }
    
    @NotNull
    public final RandomInt getBonusRolls() {
        return this.bonusRolls;
    }

    protected void createLootRoll(@NotNull Collection<ItemStack> stacks, @NotNull LootContext context) {
        ArrayList<LootEntry> list = Lists.newArrayList();
        int n = 0;
        for (LootEntry entry : this.entries) {
            if (LootCondition.testAllConditions(entry.getConditions(), context)) {
                int j = entry.getEffectiveWeight(context.getLuck());
                if (j > 0) {
                    list.add(entry);
                    n += j;
                }
            }
        }
        if (n == 0 || list.isEmpty()) {
            return;
        }
        int nextInt = context.getRandom().nextInt(n);
        for (LootEntry entry : list) {
            nextInt -= entry.getEffectiveWeight(context.getLuck());
            if (nextInt < 0) {
                entry.addLoot(stacks, context);
                break;
            }
        }
    }

    public void generateLoot(@NotNull Collection<ItemStack> collection, @NotNull LootContext context) {
        if (!LootCondition.testAllConditions(this.conditions, context)) {
            return;
        }
        for (int i = this.rolls.generateInt(context.getRandom()) + MathHelper.floor(this.bonusRolls.generateInt(context.getRandom()) * context.getLuck()), j = 0; j < i; ++j) {
            this.createLootRoll(collection, context);
        }
    }

}
