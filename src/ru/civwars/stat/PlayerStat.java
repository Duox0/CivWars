package ru.civwars.stat;

import org.bukkit.entity.Player;
import ru.civwars.entity.PlayerEntity;
import ru.lib27.annotation.NotNull;

public class PlayerStat extends BasicStat {

    public PlayerStat(@NotNull PlayerEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public PlayerEntity getEntity() {
        return (PlayerEntity) super.getEntity();
    }

    @Override
    public void apply(@NotNull Stats stat) {
        if (stat == Stats.MOVEMENT_SPEED) {
            double movementSpeed = this.getMovementSpeed();
            System.out.println("New Movement speed = " + movementSpeed);
            Player player = this.getEntity().getEntity();
            player.setWalkSpeed((float) movementSpeed);
        }
    }
}
