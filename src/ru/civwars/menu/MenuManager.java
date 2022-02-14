package ru.civwars.menu;

import org.apache.commons.lang.Validate;
import ru.civwars.CivLogger;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class MenuManager {

    private static MenuManager instance;

    public static MenuManager get() {
        return instance;
    }

    public static MenuManager init() {
        if (instance != null) {
            return instance;
        }

        CivLogger.info("Initializing [{0}] start", MenuManager.class.getSimpleName());
        instance = new MenuManager();
        CivLogger.info("Initializing [{0}] complete", MenuManager.class.getSimpleName());

        return instance;
    }

    private MenuManager() {
    }

    /**
     * Создает новое меню с заданным названием и заданного размера.
     *
     * @param name - название меню.
     * @param rows - размер инвентаря в линиях (например, 4 соответствует
     * 36-слотовому инвентарю).
     * @return созданное меню.
     */
    @NotNull
    public Menu createNewMenu(@NotNull String name, int rows) {
        Validate.isTrue(rows > 0, "The minimum number of menu rows should be 1 or more");
        Validate.isTrue(rows <= 6, "The maximum number of menu rows should be 6 or less");
        return new Menu(name, rows);
    }

    /**
     * Открывает переданное меню указанному игроку.
     *
     * @param player - игрок.
     * @param menu - меню.
     * @param page - номер страницы.
     * @return переденное меню.
     */
    @NotNull
    public Menu open(@NotNull Menu menu, @NotNull KPlayer player, int page) {
        menu.open(player, page);
        return menu;
    }

    /**
     * Открывает переданное меню указанному игроку.
     *
     * @param player - игрок.
     * @param menu - меню.
     * @return переденное меню.
     */
    @NotNull
    public Menu open(@NotNull Menu menu, @NotNull KPlayer player) {
        return this.open(menu, player, 1);
    }
}
