package ru.civwars.thread.task;

import org.bukkit.World;
import ru.civwars.schematic.Schematic;
import ru.civwars.util.BlockCoord;
import ru.lib27.annotation.NotNull;

/**
 * Ассинхронная задача.
 * Сохраняет блоки региона в файл для будущего восстановления и удаляет их.
 * Строит рамку из бедрока.
 * @author Specialist
 */
public class StartBuildTask implements Runnable {

    private static final int BLOCKS_PER_TICK = 512;
    
    private final Schematic schematic;
    
    /* Угловой блок. */
    private final BlockCoord pos;
    
    public StartBuildTask(@NotNull Schematic schematic, @NotNull BlockCoord pos) {
        this.schematic = schematic;
        this.pos = pos;
    }
    
    @Override
    public void run() {
        
    }
    
}
