package ru.civwars.thread.request.chunk;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import ru.civwars.thread.request.AsyncRequest;
import ru.civwars.thread.sync.ChunkSyncTask;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class RequestChunk extends AsyncRequest {

    protected final UUID worldId;
    protected final int x;
    protected final int z;

    public RequestChunk(@NotNull UUID worldId, int x, int z) {
        super(ChunkSyncTask.instance().getLock());
        this.worldId = worldId;
        this.x = x;
        this.z = z;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(this.worldId);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public abstract void onLoad(@NotNull Chunk chunk);
}
