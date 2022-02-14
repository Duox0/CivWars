package ru.civwars.building;

import ru.civwars.building.types.BuildingData;
import org.apache.commons.lang.Validate;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.schematic.Schematic;
import ru.civwars.util.BlockPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BuildContext<T extends BuildingData> {

    private final T data;
    private final CivWorld world;
    private final BlockPos position;
    private final EnumFacing facing;
    private final Schematic schematic;
    
    private final KPlayer player;

    private BuildContext(@NotNull Builder<T> builder) {
        this.data = builder.data;
        this.world = builder.world;
        this.position = builder.position;
        this.facing = builder.facing;
        this.schematic = builder.schematic;
        
        this.player = builder.player;
    }

    @NotNull
    public T getData() {
        return this.data;
    }

    @NotNull
    public CivWorld getWorld() {
        return this.world;
    }

    @NotNull
    public BlockPos getPosition() {
        return this.position;
    }

    @NotNull
    public EnumFacing getFacing() {
        return this.facing;
    }

    @NotNull
    public Schematic getSchematic() {
        return this.schematic;
    }

    @Nullable
    public KPlayer getPlayer() {
        return this.player;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BuildContext{");
        builder.append("data=").append(this.data.getName()).append(",");
        builder.append("world=").append(this.world.getName()).append(",");
        builder.append("position=").append(this.position.toString()).append(",");
        builder.append("facing=").append(this.facing.getName()).append(",");
        builder.append("schematic=").append(this.schematic.getFilepath());
        builder.append("}");
        return builder.toString();
    }

    public static class Builder<T extends BuildingData> {

        private final T data;

        private CivWorld world = null;
        private BlockPos position = null;
        private EnumFacing facing = EnumFacing.EAST;

        private Schematic schematic = null;
        
        private KPlayer player = null;

        public Builder(@NotNull T data) {
            this.data = data;
        }

        public Builder world(@NotNull CivWorld world) {
            this.world = world;
            return this;
        }

        public Builder position(int x, int y, int z) {
            this.position = new BlockPos(x, y, z);
            return this;
        }

        public Builder facing(@NotNull EnumFacing facing) {
            this.facing = EnumFacing.EAST;
            return this;
        }

        public Builder schematic(@NotNull Schematic schematic) {
            this.schematic = schematic;
            return this;
        }

        public Builder player(KPlayer player) {
            this.player = player;
            return this;
        }

        @NotNull
        public BuildContext<T> build() {
            Validate.notNull(this, "World can't be null");
            Validate.notNull(this, "Position can't be null");
            Validate.notNull(this, "Facing can't be null");
            Validate.notNull(this, "Schematic can't be null");
            return new BuildContext(this);
        }
    }
}
