package ru.civwars.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class Menu implements InventoryHolder {

    private final String name;
    private final int size;

    private final Map<Integer, MenuItem> items = Maps.newHashMap();
    private int totalPage = 1;

    private Inventory inventory = null;

    public Menu(@NotNull String name, int rows) {
        this.name = name;
        this.size = rows * 9;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * @return размер инвентаря.
     */
    public final int getSize() {
        return this.size;
    }

    /**
     * @return отображаемое имя инвентаря.
     */
    @NotNull
    public final String getDisplayName() {
        return this.name;
    }

    /**
     * Добавляет новый предмет в слот с указанным идентификатором.
     *
     * @param slot - идентификатор слота в инвентаре (начиная с 0).
     * @param item - предмет.
     * @return переданный предмет.
     */
    @NotNull
    public MenuItem addItem(int slot, @NotNull MenuItem item) {
        this.items.put(slot, item);

        if (slot > this.size) {
            this.totalPage = (this.items.size() - 1) / this.size;
        }

        return item;
    }

    /**
     * Добавляет новый предмет в указанный строку и столбец в инвентаре.
     *
     * @param row - номер строки (начиная с 1).
     * @param column - номер столбца (начиная с 1).
     * @param item - предмет.
     * @return переданный предмет.
     */
    @NotNull
    public MenuItem addItem(int row, int column, @NotNull MenuItem item) {
        return this.addItem((row - 1) * 9 + (column - 1), item);
    }

    /**
     * Добавляет новый предмет в инвентарь на первый свободный слот.
     *
     * @param item - предмет.
     * @return переданный предмет.
     */
    @NotNull
    public MenuItem addItem(@NotNull MenuItem item) {
        int slot = 0;
        for (int size = this.items.size() + 1; slot < size; slot++) {
            if (!this.items.containsKey(slot)) {
                break;
            }
        }
        return this.addItem(slot, item);
    }

    /**
     * Получает предмет из конкретного слота инвентаря.
     *
     * @param player - игрок.
     * @param slot - номер слота (начиная с 0).
     * @return предмет, или {@code null}, если не задано.
     */
    @Nullable
    public MenuItem getItem(@NotNull KPlayer player, int slot) {
        return this.items.get(slot - ((player.getCurrentPage() - 1) * this.size));
    }

    /**
     * Получает предмет из конкретной строчки и столбца инвентаря.
     *
     * @param player - игрок.
     * @param row - номер строки (с 1).
     * @param column - номер столбца (с 1).
     * @return предмет, или {@code null}, если не задано.
     */
    @Nullable
    public MenuItem getItem(@NotNull KPlayer player, int row, int column) {
        return this.getItem(player, (row - 1) * 9 + (column - 1));
    }

    /**
     * Открывает меню указанному игроку.
     *
     * @param player - игрок.
     * @param page - номер страницы.
     */
    public void open(@NotNull KPlayer player, int page) {
        page = Math.min(this.totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * this.size, this.items.size());

        this.inventory = Bukkit.createInventory(this, this.size, player.getName());

        int first = page * this.size;
        for (int i = first; i < lastIndex; i++) {
            MenuItem item = this.items.get(i);
            if (item == null) {
                continue;
            }

            ItemStack itemStack = item.createIcon(player);
            this.inventory.setItem(i - first, itemStack);
        }

        player.onOpenMenu(this, page);
        player.getEntity().openInventory(this.inventory);
    }

    public boolean click(KPlayer player, ClickType clickType, int slot) {
        int fixSlot = slot;
        if (slot > this.size) {
            fixSlot -= this.size;
        }

        MenuItem button = this.items.get(fixSlot);
        if (button != null) {
            ClickAction clickAction = button.getClickAction();
            if (clickAction != null) {
                clickAction.onClick(player, clickType, slot);
            }
        }

        return true;
    }

}
