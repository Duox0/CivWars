package ru.civwars.thread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.ChunkSnapshot;
import ru.civwars.CivLogger;
import ru.civwars.building.task.BuildBlock;
import ru.civwars.thread.request.chunk.RequestChunkSnapshot;
import ru.civwars.thread.request.sync.RequestBuildBlock;
import ru.civwars.thread.sync.BuildUpdateSyncTask;
import ru.civwars.thread.sync.BuildUpdateSyncTask2;
import ru.civwars.thread.sync.ChunkSyncTask;
import ru.civwars.util.ChunkCoord;
import ru.civwars.util.ChunkPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class CivAsyncTask implements Runnable {

    private static final long TIMEOUT = 5000;

     public void buildBlocks(@NotNull Queue<BuildBlock> blocks) {
         BuildUpdateSyncTask.instance().addBlocks(blocks);
    }
    
    public void buildBlocks2(@NotNull Queue<RequestBuildBlock> blocks) {
        BuildUpdateSyncTask2.instance().addRequests(blocks);
    }

    @Nullable
    public ChunkSnapshot getChunkSnapshot(@NotNull CivWorld world, @NotNull ChunkPos pos) {
        ChunkSnapshot snapshot = null;

        RequestChunkSnapshot request = new RequestChunkSnapshot(world.getId(), pos.getX(), pos.getZ());

        ChunkSyncTask.instance().getLock().lock();
        try {
            ChunkSyncTask.instance().addRequests(request);
            while (!request.isFinished()) {
                request.getCondition().await(TIMEOUT, TimeUnit.MILLISECONDS);
                if (!request.isFinished()) {
                    CivLogger.log(Level.WARNING, "Could not get chunk in {0} milliseconds! Retrying.", TIMEOUT);
                }
            }

            snapshot = request.getChunkSnapshot();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ChunkSyncTask.instance().getLock().unlock();
        }

        return snapshot;
    }

    @NotNull
    public List<ChunkSnapshot> getChunkSnapshots(@NotNull List<ChunkCoord> coords) {
        List<ChunkSnapshot> snapshots = Lists.newArrayList();

        Queue<RequestChunkSnapshot> requests = Lists.newLinkedList();
        RequestChunkSnapshot lastRequest = null;
        Iterator<ChunkCoord> iter = coords.iterator();
        while (iter.hasNext()) {
            ChunkCoord coord = iter.next();
            RequestChunkSnapshot request = new RequestChunkSnapshot(coord.getWorldId(), coord.getX(), coord.getZ());
            requests.add(request);

            if (!iter.hasNext()) {
                lastRequest = request;
            }
        }

        if (lastRequest != null) {
            ChunkSyncTask.instance().getLock().lock();
            try {
                ChunkSyncTask.instance().addRequests(requests);
                while (!lastRequest.isFinished()) {
                    lastRequest.getCondition().await(TIMEOUT, TimeUnit.MILLISECONDS);
                    if (!lastRequest.isFinished()) {
                        CivLogger.log(Level.WARNING, "Could not get chunk in {0} milliseconds! Retrying.", TIMEOUT);
                    }
                }

                for (RequestChunkSnapshot request : requests) {
                    if (request.getChunkSnapshot() != null) {
                        snapshots.add(request.getChunkSnapshot());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                ChunkSyncTask.instance().getLock().unlock();
            }

        }

        return snapshots;
    }

    @NotNull
    public Map<ChunkPos, ChunkSnapshot> getChunkSnapshots(@NotNull CivWorld world, int x, int z, int width, int height) {
        int firstChunkX = x >> 4;
        int firstChunkZ = z >> 4;
        int lastChunkX = firstChunkX + ((width - 1) >> 4) + 1;
        int lastChunkZ = firstChunkZ + ((height - 1) >> 4) + 1;

        Map<ChunkPos, ChunkSnapshot> snapshots = Maps.newHashMap();

        for (int zz = firstChunkZ; zz < lastChunkZ; zz++) {
            for (int xx = firstChunkX; xx < lastChunkX; xx++) {
                ChunkPos pos = new ChunkPos(xx, zz);
                ChunkSnapshot snapshot = this.getChunkSnapshot(world, pos);
                if (snapshot != null) {
                    snapshots.put(pos, snapshot);
                }
            }
        }

        return snapshots;
    }
}
