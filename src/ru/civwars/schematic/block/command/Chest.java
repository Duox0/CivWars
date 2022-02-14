package ru.civwars.schematic.block.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.civwars.building.Building;
import ru.civwars.building.block.BuildingChest;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;

public class Chest extends SchematicBlockCommand {

    private final int id;

    public Chest(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void onPostBuild(@NotNull Building building, @NotNull Block block) {
        BlockPos pos = new BlockPos(block);
        BuildingChest chest = building.getChest(pos);
        if (chest == null) {
            chest = new BuildingChest(this.id, pos, building);
            building.addBlock(chest);
            building.getWorld().addBuildingBlock(chest);
        }

        if (block.getType() != Material.CHEST) {
            block.setTypeIdAndData(54, BlockUtils.convertSignDataToChestData(this.getBlockData()), true);
        }
    }

    public static class Serializer extends SchematicBlockCommand.Serializer<Chest> {

        protected Serializer() {
            super("chest", Chest.class);
        }

        @NotNull
        @Override
        public String serialize(@NotNull Chest item) {
            return "";
        }

        @NotNull
        @Override
        protected Chest deserialize(@NotNull String[] args) {
            int chestId = args.length > 0 ? Utilities.asInt(args[0], 0) : 0;
            return new Chest(chestId);
        }
    }
}
