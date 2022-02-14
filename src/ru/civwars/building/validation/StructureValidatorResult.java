package ru.civwars.building.validation;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import ru.civwars.building.BuildingLayer;
import ru.civwars.schematic.Schematic;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class StructureValidatorResult {

    private final Schematic schematic;

    private final CivWorld world;
    private final BlockPos pos;

    private final boolean isValid;
    private final Map<Integer, BuildingLayer> layers;

    private final int invalidY;

    public StructureValidatorResult(@NotNull Schematic schematic, @NotNull CivWorld world, @NotNull BlockPos pos, boolean isValid, @NotNull Map<Integer, BuildingLayer> layers, int invalidY) {
        this.schematic = schematic;
        this.world = world;
        this.pos = pos;
        
        this.isValid = isValid;
        this.layers = layers;

        this.invalidY = invalidY;
    }
    
    public Schematic getSchematic() {
        return this.schematic;
    }
    
    public CivWorld getWorld() {
        return this.world;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public boolean isValid() {
        return this.isValid;
    }

    public int getInvalidY() {
        return this.invalidY;
    }
    
    @NotNull
    public Set<Integer> getLayersLevel() {
        return this.layers.keySet();
    }
    
    @Nullable
    public BuildingLayer getLayer(int y) {
        return this.layers.get(y);
    }
    
}
