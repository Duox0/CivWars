package ru.civwars.building.instance;

import java.util.UUID;
import org.bukkit.Bukkit;
import ru.civwars.building.Buildable;
import ru.civwars.building.BuildingState;
import ru.civwars.building.types.StructureData;
import ru.civwars.database.Database;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class Structure extends Buildable {

    // Build
    private int lastPercentComplete = 0;

    private long lastSaveProgress = 0;

    public Structure(@NotNull UUID id, @NotNull StructureData data, @NotNull CivWorld world) {
        super(id, data, world);
    }

    @Override
    public void changeState(@NotNull BuildingState state) {
        super.changeState(state);
        Database.getConnection().asyncExecute("UPDATE buildings SET State=? WHERE BuildingId=?", this.getState().getId(), this.getObjectId());
    }

    @Override
    public void changeBuiltBlocksCount() {
        super.changeBuiltBlocksCount();

        long time = System.currentTimeMillis();
        if (time >= (this.lastSaveProgress + 60000)) {
            this.lastSaveProgress = time;
            Database.getConnection().asyncExecute("UPDATE buildings SET BuiltBlocksCount=? WHERE BuildingId=?", this.getBuiltBlocksCount(), this.getObjectId());
        }
    }

    @Override
    public void updateProgress(@NotNull BuildingState state) {
        if (!(state == BuildingState.CLEAR_BLOCKS || state == BuildingState.BUILD)) {
            return;
        }

        boolean flag = false;
        int percentComplete = (int) (((double) this.getBuiltBlocksCount() / (double) this.getTotalBlocksCount()) * 100);
        if (percentComplete != this.lastPercentComplete) {
            flag = true;
            this.lastPercentComplete = percentComplete;
        }

        if (flag) {
            if (state == BuildingState.CLEAR_BLOCKS) {
                Bukkit.getServer().getLogger().info("Clear blocks complete on " + this.lastPercentComplete + "%");
            } else if (state == BuildingState.BUILD) {
                Bukkit.getServer().getLogger().info("Building construction complete on " + this.lastPercentComplete + "%");
            }
        }
    }

}
