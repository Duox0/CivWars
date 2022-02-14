package ru.civwars.schematic.block.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.civwars.building.Building;
import ru.civwars.building.block.BuildingControlBlock;
import ru.civwars.util.BlockPos;
import ru.lib27.annotation.NotNull;

public class Control extends SchematicBlockCommand {

    public Control() {
        super();
    }

    @Override
    public void onPostBuild(@NotNull Building building, @NotNull Block block) {
        BlockPos pos = new BlockPos(block);
        BuildingControlBlock controlBlock = building.getControlBlock(pos);
        if (controlBlock == null) {
            controlBlock = new BuildingControlBlock(pos, building);
            building.addBlock(controlBlock);
            building.getWorld().addBuildingBlock(controlBlock);
        }

        controlBlock.setHitpoints(1);
        
        if (block.getType() != Material.OBSIDIAN) {
            block.setTypeIdAndData(49, (byte) 0, true);
        }
    }

    public static class Serializer extends SchematicBlockCommand.Serializer<Control> {

        protected Serializer() {
            super("control", Control.class);
        }

        @NotNull
        @Override
        public String serialize(@NotNull Control item) {
            return "";
        }

        @NotNull
        @Override
        protected Control deserialize(@NotNull String[] args) {
            return new Control();
        }
    }
}
