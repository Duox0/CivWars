package ru.civwars.menu;

import org.bukkit.event.inventory.ClickType;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public interface ClickAction {

    void onClick(@NotNull KPlayer player, @NotNull ClickType clickType, int slot);

}
