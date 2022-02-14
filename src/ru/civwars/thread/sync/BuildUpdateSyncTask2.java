package ru.civwars.thread.sync;

import com.google.common.collect.Lists;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.thread.request.sync.RequestBuildBlock;
import ru.lib27.annotation.NotNull;

public class BuildUpdateSyncTask2 implements Runnable {

    private static BuildUpdateSyncTask2 instance;

    public static BuildUpdateSyncTask2 instance() {
        return instance;
    }

    public static void init(@NotNull CivWars civcraft) {
        instance = new BuildUpdateSyncTask2(civcraft);
    }
    
    private final CivWars civcraft;

    private int limit = Integer.MAX_VALUE;
    private final Queue<RequestBuildBlock> requests = Lists.newLinkedList();

    private final ReentrantLock lock = new ReentrantLock();

    private BuildUpdateSyncTask2(@NotNull CivWars civcraft) {
        this.civcraft = civcraft;
    }

    public void addRequests(@NotNull Queue<RequestBuildBlock> requests) {
        this.lock.lock();
        try {
            this.requests.addAll(requests);
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
                    RequestBuildBlock next = this.requests.poll();
                    if (next == null) {
                        break;
                    }

                    next.update();
                }
            } finally {
                this.lock.unlock();
            }
        } else {
            CivLogger.log(Level.WARNING, "Could not get sync build update lock, skipping until next tick.");
        }
    }

}
