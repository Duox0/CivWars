package ru.civwars.thread.sync;

import com.google.common.collect.Lists;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.thread.request.chunk.RequestChunk;
import ru.lib27.annotation.NotNull;

public class ChunkSyncTask implements Runnable {

    private static ChunkSyncTask instance;

    public static ChunkSyncTask instance() {
        return instance;
    }

    public static void init(@NotNull CivWars civcraft) {
        instance = new ChunkSyncTask(civcraft);
    }

    private final CivWars civcraft;

    private int limit = 2048;
    private final Queue<RequestChunk> requests = Lists.newLinkedList();

    private final ReentrantLock lock = new ReentrantLock();

    private ChunkSyncTask(@NotNull CivWars civcraft) {
        this.civcraft = civcraft;
    }

    public void addRequests(@NotNull RequestChunk request) {
        this.requests.add(request);
    }
    
    public void addRequests(@NotNull Queue<? extends RequestChunk> requests) {
        this.requests.addAll(requests);
    }
    
    @NotNull
    public ReentrantLock getLock() {
        return this.lock;
    }

    @Override
    public void run() {
        if (this.lock.tryLock()) {
            try {

                int i = 0;
                for (i = 0; i < this.limit; i++) {
                    RequestChunk next = this.requests.poll();
                    if (next == null) {
                        break;
                    }

                    World world = next.getWorld();
                    if (world == null) {
                        continue;
                    }

                    Chunk chunk = world.getChunkAt(next.getX(), next.getZ());

                    if (!chunk.isLoaded()) {
                        if (!chunk.load()) {
                            CivLogger.log(Level.SEVERE, "Could not load chunk at {0},{1}", new Object[]{next.getX(), next.getZ()});
                            continue;
                        }
                    }

                    next.onLoad(chunk);
                    next.finished();
                    next.getCondition().signalAll();
                }
            } finally {
                this.lock.unlock();
            }
        } else {
        }
    }

}
