package ru.civwars.building.block;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import ru.civwars.building.Building;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class BuildingBlock {

    protected final BlockPos position;

    protected final Building building;

    public BuildingBlock(@NotNull BlockPos position, @NotNull Building building) {
        this.position = position;
        this.building = building;
    }

    @NotNull
    public BlockPos getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "BuildingBlock{position=" + this.position.toString() + ",buildingId=" + this.building.getObjectId() + "}";
    }

    public final void damage(@NotNull CivWorld world, @NotNull KPlayer player, int damage) {
        if (damage <= 0) {
            return;
        }

        if (!this.isDamaged()) {
            return;
        }

        this.damageBlock(world, player, damage);
    }

    protected boolean isDamaged() {
        return this.building.isDamagedBlock(this);
    }

    protected boolean damageBlock(@NotNull CivWorld world, @NotNull KPlayer player, int damage) {
        if (this.building.damage(player, damage)) {
            this.onHit(world, player);
            return true;
        }
        return false;
    }

    protected void onHit(@NotNull CivWorld world, @NotNull KPlayer player) {
        Location loc = new Location(world.getWorld(), this.position.getX(), this.position.getY(), this.position.getZ());

        world.getWorld().playSound(loc, Sound.BLOCK_ANVIL_USE, 0.2f, 1);
        world.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
        player.sendRawMessage(this.building.getHealth() + "/" + 100);
    }
}
