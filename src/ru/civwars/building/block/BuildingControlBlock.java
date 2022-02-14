package ru.civwars.building.block;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import ru.civwars.building.Building;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class BuildingControlBlock extends BuildingBlock {

    private int hitpoints = 0;

    public BuildingControlBlock(@NotNull BlockPos position, @NotNull Building building) {
        super(position, building);
    }

    /**
     * Устанавливает прочность блока.
     *
     * @param newHitpoints - новая прочность блока.
     */
    public void setHitpoints(int newHitpoints) {
        this.hitpoints = Math.max(0, newHitpoints);
    }

    /**
     *
     * @return прочность блока.
     */
    public int getHitpoints() {
        return this.hitpoints;
    }

    /**
     * Уничтожен ли блок.
     *
     * @return {@code true}, если уничтожен. Иначе {@code false}.
     */
    public boolean isDestroyed() {
        return this.hitpoints <= 0;
    }

    @Override
    public String toString() {
        return "BuildingControlBlock{position=" + this.position.toString() + ",buildingId=" + this.building.getObjectId() + ",hitpoints=" + this.hitpoints + "}";
    }

    @Override
    protected boolean isDamaged() {
        return (this.isDestroyed() && super.isDamaged());
    }

    @Override
    protected boolean damageBlock(@NotNull CivWorld world, @NotNull KPlayer player, int damage) {
        if (!super.damageBlock(world, player, damage)) {
            return false;
        }

        this.hitpoints -= damage;
        if (this.hitpoints <= 0) {
            this.hitpoints = 0;
            this.onDestroy(world, player);
            this.building.onControlBlockDestroy(this, world, player);
        }
        return true;
    }

    @Override
    protected void onHit(@NotNull CivWorld world, @NotNull KPlayer player) {
        Location loc = new Location(world.getWorld(), this.position.getX(), this.position.getY(), this.position.getZ());

        world.getWorld().playSound(loc, Sound.BLOCK_ANVIL_USE, 0.2f, 1);
        world.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
        player.sendRawMessage(this.building.getHealth() + "/" + 100);
    }

    protected void onDestroy(@NotNull CivWorld world, @NotNull KPlayer player) {
        Location loc = new Location(world.getWorld(), this.position.getX(), this.position.getY(), this.position.getZ());

        Block block = loc.getBlock();
        block.setType(Material.AIR);
        world.getWorld().playSound(loc, Sound.BLOCK_ANVIL_BREAK, 1.0f, -1.0f);
        world.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }

}
