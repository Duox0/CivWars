package ru.civwars.bukkit.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public enum BukkitItemSlot {
    MAIN_HAND("main_hand", EquipmentSlot.HAND, 0),
    OFF_HAND("off_hand", EquipmentSlot.OFF_HAND, 40),
    HEAD("head", EquipmentSlot.HEAD, 39),
    CHEST("chest", EquipmentSlot.CHEST, 38),
    FEET("feet", EquipmentSlot.FEET, 37),
    LEGS("legs", EquipmentSlot.LEGS, 36);

    private final String name;
    private final EquipmentSlot slot;
    private final int i;

    private BukkitItemSlot(@NotNull String name, @NotNull EquipmentSlot slot, int i) {
        this.name = name;
        this.slot = slot;
        this.i = i;
    }

    public int getSlot() {
        return this.i;
    }

    @Nullable
    public ItemStack getItemStackFromSlot(@NotNull Player player, @NotNull BukkitItemSlot slot) {
        switch (slot) {
            case MAIN_HAND:
                return player.getInventory().getItemInMainHand();
            case OFF_HAND:
                return player.getInventory().getItemInOffHand();
            case HEAD:
                return player.getInventory().getHelmet();
            case CHEST:
                return player.getInventory().getChestplate();
            case LEGS:
                return player.getInventory().getLeggings();
            case FEET:
                return player.getInventory().getBoots();
        }
        return null;
    }

    @Nullable
    public static BukkitItemSlot getEquipmentSlot(@NotNull String name) {
        for (BukkitItemSlot type : BukkitItemSlot.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    @NotNull
    public static BukkitItemSlot getEquipmentSlot(@NotNull EquipmentSlot slot) {
        for (BukkitItemSlot type : BukkitItemSlot.values()) {
            if (type.slot == slot) {
                return type;
            }
        }
        return MAIN_HAND;
    }

    @Nullable
    public static BukkitItemSlot getSlot(@NotNull InventoryType.SlotType slotType, int slot) {
        if (slotType == InventoryType.SlotType.ARMOR) {
            switch (slot) {
                case 36:
                    return BukkitItemSlot.FEET;
                case 37:
                    return BukkitItemSlot.LEGS;
                case 38:
                    return BukkitItemSlot.CHEST;
                case 39:
                    return BukkitItemSlot.HEAD;
                default:
                    return null;
            }
        } else if (slotType == InventoryType.SlotType.QUICKBAR) {
            switch (slot) {
                case 40:
                    return BukkitItemSlot.OFF_HAND;
                default:
                    return null;
            }
        }
        return null;
    }
    
    @Nullable
    public  static BukkitItemSlot getSlot(ItemStack stack) {
        if (ItemUtils.isEmpty(stack)) {
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
    
}
