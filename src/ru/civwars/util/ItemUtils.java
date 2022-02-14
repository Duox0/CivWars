package ru.civwars.util;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class ItemUtils {

    private static final List<Material> HELMETS = Lists.newArrayList();
    private static final List<Material> HATS = Lists.newArrayList();
    private static final List<Material> CHESTPLATES = Lists.newArrayList();
    private static final List<Material> LEGGINGS = Lists.newArrayList();
    private static final List<Material> BOOTS = Lists.newArrayList();

    static {
        HELMETS.add(Material.LEATHER_HELMET);
        HELMETS.add(Material.CHAINMAIL_HELMET);
        HELMETS.add(Material.IRON_HELMET);
        HELMETS.add(Material.GOLD_HELMET);
        HELMETS.add(Material.DIAMOND_HELMET);

        HATS.addAll(HELMETS);
        HATS.add(Material.PUMPKIN);

        CHESTPLATES.add(Material.LEATHER_CHESTPLATE);
        CHESTPLATES.add(Material.CHAINMAIL_CHESTPLATE);
        CHESTPLATES.add(Material.IRON_CHESTPLATE);
        CHESTPLATES.add(Material.GOLD_CHESTPLATE);
        CHESTPLATES.add(Material.DIAMOND_CHESTPLATE);

        LEGGINGS.add(Material.LEATHER_LEGGINGS);
        LEGGINGS.add(Material.CHAINMAIL_LEGGINGS);
        LEGGINGS.add(Material.IRON_LEGGINGS);
        LEGGINGS.add(Material.GOLD_LEGGINGS);
        LEGGINGS.add(Material.DIAMOND_LEGGINGS);

        BOOTS.add(Material.LEATHER_BOOTS);
        BOOTS.add(Material.CHAINMAIL_BOOTS);
        BOOTS.add(Material.IRON_BOOTS);
        BOOTS.add(Material.GOLD_BOOTS);
        BOOTS.add(Material.DIAMOND_BOOTS);

    }

    public static boolean isEmpty(ItemStack stack) {
        return (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0);
    }

    public static boolean isStackable(ItemStack stack) {
        return stack != null && stack.getType().getMaxStackSize() > 1 && (!ItemUtils.isItemStackDamageable(stack) || !ItemUtils.h(stack));
    }
    
    public static boolean h(ItemStack stack) {
        return ItemUtils.isItemStackDamageable(stack) && stack.getDurability() > 0;
    }

    public static boolean isItemStackDamageable(ItemStack stack) {
        return !ItemUtils.isEmpty(stack) && stack.getType().getMaxDurability() > 0 && (!stack.hasItemMeta() || !stack.getItemMeta().isUnbreakable());
    }

    public static boolean attemptDamageItem(ItemStack stack, short damage, @NotNull Random random, @Nullable Player player) {
        if (!ItemUtils.isItemStackDamageable(stack)) {
            return false;
        }

        if (damage > 0) {
            int level = stack.getEnchantmentLevel(Enchantment.DURABILITY);
            for (int i = 0; level > 0 && i < damage; ++i) {
                if (random.nextFloat() >= 0.6f && random.nextInt(damage + 1) > 0) {
                    damage -= 1;
                }
            }
            if (damage <= 0) {
                return false;
            }
            stack.setDurability((short) (stack.getDurability() + damage));
        }
        return stack.getDurability() > stack.getType().getMaxDurability();
    }

    public static boolean damageItem(ItemStack stack, int damage, @NotNull Random random, @NotNull Player player) {
        if (player.getGameMode() != GameMode.CREATIVE && ItemUtils.isItemStackDamageable(stack) && ItemUtils.attemptDamageItem(stack, (short) damage, random, player)) {
            EntityUtils.renderBrokenItemStack(player, stack);
            return true;
        }
        return false;
    }

    public static void dropItemStacks(@NotNull Collection<ItemStack> stacks, @NotNull Location location) {
        stacks.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != Material.AIR)
                .forEach(item -> {
                    location.getWorld().dropItem(location, item).setVelocity(new Vector(0, 0, 0));
                });
    }

    @NotNull
    public static ItemStack splitStack(@NotNull ItemStack stack, int count) {
        int i = Math.min(count, stack.getAmount());
        ItemStack out = stack.clone();
        out.setAmount(i);
        stack.setAmount(stack.getAmount() - i);
        return out;
    }

    @Nullable
    public static BukkitItemSlot getSlotFromItemStack(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return null;
        }

        switch (stack.getType()) {
            case LEATHER_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
            case DIAMOND_HELMET:
                return BukkitItemSlot.HEAD;
            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return BukkitItemSlot.CHEST;
            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case GOLD_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return BukkitItemSlot.LEGS;
            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case GOLD_BOOTS:
            case DIAMOND_BOOTS:
                return BukkitItemSlot.FEET;
        }
        return null;
    }

    @Nullable
    public static boolean isHelmet(ItemStack stack) {
        return stack != null ? HELMETS.contains(stack.getType()) : false;
    }

    @Nullable
    public static boolean isHat(ItemStack stack) {
        return stack != null ? HATS.contains(stack.getType()) : false;
    }

    @Nullable
    public static boolean isChestplate(ItemStack stack) {
        return stack != null ? CHESTPLATES.contains(stack.getType()) : false;
    }

    @Nullable
    public static boolean isLeggings(ItemStack stack) {
        return stack != null ? LEGGINGS.contains(stack.getType()) : false;
    }

    @Nullable
    public static boolean isBoots(ItemStack stack) {
        return stack != null ? BOOTS.contains(stack.getType()) : false;
    }

}
