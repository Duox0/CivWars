package ru.civwars.building.validation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import ru.civwars.CivWars;
import ru.civwars.building.Building;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.schematic.Schematic;
import ru.civwars.building.BuildingLayer;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.DecimalHelper;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class StructureValidator {

    private static CivWars plugin;

    private static final ExecutorService executors = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Building Validator Thread - #%d").build());

    public static void init(@NotNull CivWars plugin) {
        if (StructureValidator.plugin != null) {
            return;
        }
        StructureValidator.plugin = plugin;
    }

    private StructureValidator() {
    }

    @NotNull
    public static void validate(@NotNull AbstractCommandSender sender, @NotNull Schematic schematic, @NotNull CivWorld world, @NotNull BlockPos pos, @Nullable Consumer<StructureValidatorResult> consumer) {
        sender.sendRow("message_structure_validator_checking_position");
        StructureValidator.executors.submit(new StructureValidatorTask(schematic, world, pos, (StructureValidatorResult result) -> {
            if (result.isValid()) {
                sender.sendSuccess("building_validator_success");
            } else {
                sender.sendError("building_validator_failed");
                if (result.getInvalidY() != -1) {
                    // reason invalid layer
                    BuildingLayer layer = result.getLayer(result.getInvalidY());
                    if (layer != null) {
                        double percentValid = ((double) layer.getReinforcement()) / ((double) layer.getTotalBlocksCount());
                        sender.sendError("building_validator_invalid_layer",
                                result.getInvalidY(), DecimalHelper.formatPercentage(percentValid),
                                layer.getReinforcement(), layer.getTotalBlocksCount(),
                                DecimalHelper.formatPercentage(BlockUtils.getReinforcementForLevel(pos.getY() - result.getInvalidY() - 1)));
                    }
                }
            }

            if (consumer != null) {
                consumer.accept(result);
            }
        }));
    }

    @Nullable
    public static void validate(@NotNull Building building) {
        if (building.getSchematicPath() != null) {
            Schematic schematic = SchematicManager.getSchematic(building.getSchematicPath());
            if (schematic != null) {
                StructureValidator.executors.submit(new StructureValidatorTask(schematic, building.getWorld(), building.getCoord(), (StructureValidatorResult result) -> {
                    building.setIsValidated(result);
                }));
            }
        }
    }
}
