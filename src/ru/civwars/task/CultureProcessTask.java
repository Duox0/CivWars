package ru.civwars.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.building.Building;
import ru.civwars.building.instance.TownHall;
import ru.civwars.town.CultureChunk;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.ChunkCoord;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class CultureProcessTask implements Runnable {

    private static final int[][] OFFSET = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private static CultureProcessTask instance;

    public static CultureProcessTask get() {
        return instance;
    }

    public static CultureProcessTask init() {
        if (instance == null) {
            instance = new CultureProcessTask();
        }
        return instance;
    }

    public static void processCulture(int chunks) {
        CultureProcessTask.instance.lock.lock();
        try {
            CultureProcessTask.instance.needUpdate = true;
            CultureProcessTask.instance.chunks = chunks;
        } finally {
            CultureProcessTask.instance.lock.unlock();
        }
    }

    private final ReentrantLock lock = new ReentrantLock();
    private boolean needUpdate = true;
    private int chunks = 6;

    private CultureProcessTask() {
    }

    @Override
    public void run() {
        this.lock.lock();
        try {
            if (!this.needUpdate) {
                return;
            }
            this.needUpdate = false;
        } finally {
            this.lock.unlock();
        }

        this.process(this.chunks);
    }

    private void process(int chunks) {
        TownManager.getTowns().stream().forEach(t -> {
            try {
                this.process(t, chunks);
            } catch (Exception ex) {
                CivLogger.log(Level.SEVERE, "Exception generated during culture process for town:" + t.getName(), ex);
            }
        });

        TownManager.getTowns().stream().forEach(t -> {
            try {
                this.processTouchingCultures(t);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void process(@NotNull Town town, int chunks) {
        Set<ChunkCoord> cultureChunks = Sets.newHashSet();
        town.getBuildings().stream().filter(s -> (s instanceof TownHall)).forEach(s -> this.process(town, s, chunks, cultureChunks));
        town.trimCultureChunks(cultureChunks);
    }

    private void process(@NotNull Town town, @NotNull Building building, int chunks, @NotNull Set<ChunkCoord> cultureChunks) {
        BlockPos centerPos = building.getCenterCoord();
        ChunkCoord coord = new ChunkCoord(building.getWorld().getId(), centerPos.getX() >> 4, centerPos.getZ() >> 4);
        CultureChunk first = building.getWorld().getCultureChunk(coord);
        if (first == null) {
            first = new CultureChunk(coord, town);
            first.getTown().addCultureChunk(first);
            building.getWorld().addCultureChunk(first);
        } else if (first.getTown() != town) {
            first.getTown().removeCultureChunk(coord);
            first.setTown(town);
            first.getTown().addCultureChunk(first);
        }

        int chunksSize = chunks;

        Queue<CultureChunk> queue = Lists.newLinkedList();
        Map<ChunkCoord, CultureChunk> closed = Maps.newHashMap();

        queue.add(first);
        while (!queue.isEmpty()) {
            CultureChunk node = queue.poll();

            if (closed.containsKey(node.getCoord())) {
                continue;
            }

            if (node.getCoord().manhattanDistance(coord) > chunksSize) {
                break;
            }

            closed.put(node.getCoord(), node);

            for (int i = 0; i < 4; i++) {
                ChunkCoord nextCoord = new ChunkCoord(building.getWorld().getId(), node.getX() + OFFSET[i][0], node.getZ() + OFFSET[i][1]);
                if (closed.containsKey(nextCoord)) {
                    continue;
                }

                CultureChunk neighbor = building.getWorld().getCultureChunk(nextCoord);
                if (neighbor == null) {
                    if ((nextCoord.manhattanDistance(coord)) < chunksSize) {
                        neighbor = new CultureChunk(nextCoord, town);
                        town.addCultureChunk(neighbor);
                        building.getWorld().addCultureChunk(neighbor);
                        cultureChunks.add(neighbor.getCoord());
                    } else {
                        continue;
                    }
                } else if (neighbor.getTown() != town) {
                    double nodePower = node.getPower();
                    double neighborPower = neighbor.getPower();
                    boolean switchOwners = false;

                    if (nodePower > neighborPower) {
                        switchOwners = true;

                    } else if (nodePower == neighborPower) {
                    }

                    if (switchOwners) {
                        neighbor.getTown().removeCultureChunk(neighbor);
                        neighbor.setTown(node.getTown());
                        neighbor.getTown().addCultureChunk(neighbor);
                        cultureChunks.add(neighbor.getCoord());
                    } else {
                        continue;
                    }
                } else if ((nextCoord.manhattanDistance(coord)) < chunksSize) {
                    cultureChunks.add(neighbor.getCoord());
                }

                neighbor.setDistance(neighbor.getCoord().manhattanDistance(coord));

                queue.add(neighbor);
            }
        }

    }

    private void processTouchingCultures(@NotNull Town town) {
        town.clearBorderTowns();

        Map<ChunkCoord, Boolean> closed = Maps.newHashMap();
        for (CultureChunk node : town.getCultureChunks()) {
            closed.put(node.getCoord(), true);
            CivWorld world = node.getWorld();
            if (world == null) {
                continue;
            }

            for (int i = 0; i < 4; i++) {
                ChunkCoord nextCoord = new ChunkCoord(world.getId(), node.getX() + OFFSET[i][0], node.getZ() + OFFSET[i][1]);
                if (closed.containsKey(nextCoord)) {
                    continue;
                }

                closed.put(nextCoord, true);

                CultureChunk next = world.getCultureChunk(nextCoord);
                if (next == null || (next.getTown() == node.getTown())) {
                    continue;
                }

                town.addBorderTown(next.getTown());
            }
        }
    }

}
