package ru.civwars.schematic;

import ru.civwars.schematic.block.SchematicBlocks;
import ru.civwars.schematic.block.SchematicBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import ru.civwars.CivWars;
import ru.civwars.schematic.block.command.SchematicCommands;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class SchematicManager {

    private static CivWars plugin;
    private static final ConcurrentHashMap<String, Schematic> cache = new ConcurrentHashMap();

    public static void init(@NotNull CivWars plugin) {
        if (SchematicManager.plugin != null) {
            return;
        }
        SchematicManager.plugin = plugin;
    }

    private SchematicManager() {
    }

    @Nullable
    public static Schematic getSchematic(@NotNull String filepath, boolean load) {
        Schematic schematic = SchematicManager.cache.get(filepath);
        if (schematic == null && load) {
            try {
                schematic = SchematicManager.loadSchematic(filepath);
                SchematicManager.cache.put(schematic.getFilepath(), schematic);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return schematic;
    }

    @Nullable
    public static Schematic getSchematic(@NotNull String filepath) {
        return SchematicManager.getSchematic(filepath, true);
    }

    @NotNull
    public static Schematic loadSchematic(@NotNull File file) throws SchematicException {
        Schematic schematic = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            String[] split = line.split(";");

            int width = Integer.parseInt(split[0]);
            int height = Integer.parseInt(split[1]);
            int length = Integer.parseInt(split[2]);
            int yShift = split.length > 3 ? Integer.parseInt(split[3]) : 0;
            SchematicBlock[][][] blocks = new SchematicBlock[width][height][length];

            while ((line = reader.readLine()) != null) {
                //0:0:0,1:0
                //x:y:z,id:data
                split = line.split(",");
                String[] strLoc = split[0].split(":");
                String[] strBlock = split[1].split(":");

                //Parse location
                int blockX = Integer.parseInt(strLoc[0]);
                int blockY = Integer.parseInt(strLoc[1]);
                int blockZ = Integer.parseInt(strLoc[2]);

                // Parse type
                int blockId = Integer.parseInt(strBlock[0]);
                byte blockData = Byte.parseByte(strBlock[1]);

                SchematicBlock block = null;
                if ((blockId == 63 || blockId == 68) && split.length > 2) {
                    if (split[2].startsWith("/")) {
                        try {
                            block = SchematicCommands.getSerializerForName(split[2].substring(1)).deserialize(blockId, blockData, Utilities.subargs(split, 3));
                        } catch (Exception ex) {
                            //CivLogger.log(Level.WARNING, ex.getMessage());
                        }
                    }
                }

                if (block == null) {
                    block = blockId > 0 ? SchematicBlocks.getSerializerForName(blockId).deserialize(blockId, blockData, Utilities.subargs(split, 2)) : SchematicBlock.AIR;
                }
                blocks[blockX][blockY][blockZ] = block;
            }

            schematic = new Schematic(file.getPath(), yShift, blocks);
        } catch (Exception ex) {
            throw new SchematicException("Invalid Schematic", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }

        if (schematic == null) {
            throw new SchematicException("Invalid Schematic");
        }

        return schematic;
    }

    @NotNull
    public static Schematic loadSchematic(@NotNull String filepath) throws SchematicException {
        return SchematicManager.loadSchematic(new File(filepath));
    }
}
