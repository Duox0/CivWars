package ru.civwars.thread.sync;

import com.google.common.collect.Lists;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.building.task.BuildBlock;
import ru.lib27.annotation.NotNull;

public class BuildUpdateSyncTask implements Runnable {

    private static BuildUpdateSyncTask instance;

    public static BuildUpdateSyncTask instance() {
        return instance;
    }

    public static void init(@NotNull CivWars civcraft) {
        instance = new BuildUpdateSyncTask(civcraft);
    }

    private final CivWars civcraft;

    private int limit = Integer.MAX_VALUE;
    private final Queue<BuildBlock> blocks = Lists.newLinkedList();

    private final ReentrantLock lock = new ReentrantLock();

    private BuildUpdateSyncTask(@NotNull CivWars civcraft) {
        this.civcraft = civcraft;
    }

    public void addBlocks(@NotNull Queue<BuildBlock> blocks) {
        this.lock.lock();
        try {
            this.blocks.addAll(blocks);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void run() {
        if (this.lock.tryLock()) {
            try {

                int i = 0;
                for (i = 0; i < this.limit; i++) {
                    BuildBlock next = this.blocks.poll();
                    if (next == null) {
                        break;
                    }
                    next.buildBlock();
                }
            } finally {
                this.lock.unlock();
            }
        } else {
            CivLogger.log(Level.WARNING, "Could not get sync build update lock, skipping until next tick.");
        }
    }

}
