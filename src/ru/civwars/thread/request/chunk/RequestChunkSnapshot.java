package ru.civwars.thread.request.chunk;

import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RequestChunkSnapshot extends RequestChunk {

    private ChunkSnapshot snapshot = null;
    
    public RequestChunkSnapshot(@NotNull UUID worldId, int x, int z) {
       super(worldId, x, z);
    }

    @Nullable
    public ChunkSnapshot getChunkSnapshot() {
        return snapshot;
    }

    @Override
    public void onLoad(@NotNull Chunk chunk) {
        this.snapshot = chunk.getChunkSnapshot();
    }
}
