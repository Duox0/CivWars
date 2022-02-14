package ru.civwars.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import ru.civwars.CivWars;
import ru.lib27.annotation.NotNull;

public class BlockUtils {

    private static BlockUtils instance;

    public static BlockUtils instance() {
        return instance;
    }

    public static void init(@NotNull CivWars civcraft) {
        if (instance != null) {
            return;
        }
        instance = new BlockUtils(civcraft);
    }

    private final CivWars civcraft;
    private final Map<Integer, Boolean> attachableBlocks = Maps.newHashMap();
    private final Map<Integer, Boolean> bannedBlocksForUndo = Maps.newHashMap();
    private final Map<Integer, Integer> reinforcements = Maps.newHashMap();

    private BlockUtils(@NotNull CivWars civcraft) {
        this.civcraft = civcraft;
    }

    public void load() {
        this.loadAttachableBlocks();
        this.loadBannedBlocksForUndo();
        this.loadReinforcementsBlocks();
    }

    private void loadAttachableBlocks() {
        this.attachableBlocks.clear();
        File file = new File(this.civcraft.getDataFolder(), "data/attachable_blocks.json");
        if (!(file.exists() && file.isFile())) {
            return;
        }

        try {
            JsonArray array = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonArray();
            for (JsonElement element : array) {
                this.attachableBlocks.put(JsonUtils.getInt(element, "attachable block"), true);
            }
        } catch (Throwable thrwbl) {
            thrwbl.printStackTrace();
        }
    }

    private void loadBannedBlocksForUndo() {
        this.bannedBlocksForUndo.clear();
        File file = new File(this.civcraft.getDataFolder(), "data/banned_blocks_for_undo.json");
        if (!(file.exists() && file.isFile())) {
            return;
        }

        try {
            JsonArray array = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonArray();
            for (JsonElement element : array) {
                this.bannedBlocksForUndo.put(JsonUtils.getInt(element, "banned block for undo"), true);
            }
        } catch (Throwable thrwbl) {
            thrwbl.printStackTrace();
        }
    }

    private void loadReinforcementsBlocks() {
        this.reinforcements.clear();
        File file = new File(this.civcraft.getDataFolder(), "data/reinforcements.json");
        if (!(file.exists() && file.isFile())) {
            return;
        }

        try {
            JsonArray array = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = JsonUtils.getJsonObject(element, "block reinforcement");
                this.reinforcements.put(JsonUtils.getInt(object, "block_id"), JsonUtils.getInt(object, "reinforcement"));
            }
        } catch (Throwable thrwbl) {
            thrwbl.printStackTrace();
        }
    }

    public static boolean isAttachable(int blockId) {
        return BlockUtils.instance.attachableBlocks.getOrDefault(blockId, false);
    }

    public static boolean isAllowedBlockForUndo(int blockId) {
        return BlockUtils.instance.bannedBlocksForUndo.getOrDefault(blockId, false);
    }

    public static int getReinforcementValue(int blockId) {
        return BlockUtils.instance.reinforcements.getOrDefault(blockId, 1);
    }

    public static double getReinforcementForLevel(int level) {
        if (level > 10) {
            return 0.3;
        }

        if (level > 40) {
            return 0.1;
        }

        return 0.8;
    }

    private static final List<Material> blocks = Lists.newArrayList();

    static {
        //blocks.add(Material.AIR);
        blocks.add(Material.STONE);
        blocks.add(Material.GRASS);
        blocks.add(Material.DIRT);
        blocks.add(Material.COBBLESTONE);
        blocks.add(Material.WOOD);
        //blocks.add(Material.BEDROCK);
        //blocks.add(Material.WATER);
        //blocks.add(Material.STATIONARY_WATER);
        //blocks.add(Material.LAVA);
        //blocks.add(Material.STATIONARY_LAVA);
        blocks.add(Material.SAND);
        blocks.add(Material.GRAVEL);
        blocks.add(Material.GOLD_ORE);
        blocks.add(Material.IRON_ORE);
        blocks.add(Material.COAL_ORE);
        blocks.add(Material.LOG);
        //blocks.add(Material.LEAVES);
        blocks.add(Material.SPONGE);
        //blocks.add(Material.GLASS);
        blocks.add(Material.LAPIS_ORE);
        //blocks.add(Material.LAPIS_BLOCK);
        //blocks.add(Material.DISPENSER);
        blocks.add(Material.SANDSTONE);
        //blocks.add(Material.NOTE_BLOCK);
        //blocks.add(Material.BED_BLOCK);
        //blocks.add(Material.PISTON_STICKY_BASE);
        //blocks.add(Material.WEB);
        //blocks.add(Material.LONG_GRASS);
        //blocks.add(Material.DEAD_BUSH);
        //blocks.add(Material.PISTON_BASE);
        //blocks.add(Material.PISTON_EXTENSION);
        blocks.add(Material.WOOL);
        //blocks.add(Material.PISTON_MOVING_PIECE);
        //blocks.add(Material.YELLOW_FLOWER);
        //blocks.add(Material.RED_ROSE);
        //blocks.add(Material.BROWN_MUSHROOM);
        //blocks.add(Material.RED_MUSHROOM);
        //blocks.add(Material.GOLD_BLOCK);
        //blocks.add(Material.IRON_BLOCK);
        //blocks.add(Material.DOUBLE_STEP);
        //blocks.add(Material.STEP);
        blocks.add(Material.BRICK);
        //blocks.add(Material.TNT);
        //blocks.add(Material.BOOKSHELF);
        blocks.add(Material.MOSSY_COBBLESTONE);
        blocks.add(Material.OBSIDIAN);
        //blocks.add(Material.MOB_SPAWNER);
        //blocks.add(Material.WOOD_STAIRS);
        blocks.add(Material.CHEST);
        blocks.add(Material.DIAMOND_ORE);
        //blocks.add(Material.DIAMOND_BLOCK);
        //blocks.add(Material.WORKBENCH);
        //blocks.add(Material.CROPS);
        //blocks.add(Material.SOIL);
        //blocks.add(Material.FURNACE);
        //blocks.add(Material.SIGN_POST);
        //blocks.add(Material.WOODEN_DOOR);
        //blocks.add(Material.LADDER);
        //blocks.add(Material.RAILS);
        //blocks.add(Material.COBBLESTONE_STAIRS);
        //blocks.add(Material.WALL_SIGN);
        //blocks.add(Material.LEVER);
        //blocks.add(Material.STONE_PLATE);
        //blocks.add(Material.IRON_DOOR_BLOCK);
        //blocks.add(Material.WOOD_PLATE);
        blocks.add(Material.REDSTONE_ORE);
        //blocks.add(Material.GLOWING_REDSTONE_ORE);
        //blocks.add(Material.REDSTONE_TORCH_OFF);
        //blocks.add(Material.STONE_BUTTON);
        //blocks.add(Material.SNOW);
        blocks.add(Material.ICE);
        blocks.add(Material.SNOW_BLOCK);
        //blocks.add(Material.CACTUS);
        blocks.add(Material.CLAY);
        //blocks.add(Material.SUGAR_CANE_BLOCK);
        //blocks.add(Material.JUKEBOX);
        //blocks.add(Material.FENCE);
        blocks.add(Material.PUMPKIN);
        blocks.add(Material.NETHERRACK);
        blocks.add(Material.SOUL_SAND);
        blocks.add(Material.GLOWSTONE);
        //blocks.add(Material.PORTAL);
        //blocks.add(Material.JACK_O_LANTERN);
        //blocks.add(Material.CAKE_BLOCK);
        //blocks.add(Material.DIODE_BLOCK_OFF);
        //blocks.add(Material.DIODE_BLOCK_ON);
        //blocks.add(Material.STAINED_GLASS);
        //blocks.add(Material.TRAP_DOOR);
        //blocks.add(Material.MONSTER_EGGS);
        blocks.add(Material.SMOOTH_BRICK);
        blocks.add(Material.HUGE_MUSHROOM_1);
        blocks.add(Material.HUGE_MUSHROOM_2);
        //blocks.add(Material.IRON_FENCE);
        //blocks.add(Material.THIN_GLASS);
        blocks.add(Material.MELON_BLOCK);
        //blocks.add(Material.PUMPKIN_STEM);
        //blocks.add(Material.MELON_STEM);
        //blocks.add(Material.VINE);
        //blocks.add(Material.FENCE_GATE);
        //blocks.add(Material.BRICK_STAIRS);
        //blocks.add(Material.SMOOTH_STAIRS);
        blocks.add(Material.MYCEL);
        //blocks.add(Material.WATER_LILY);
        blocks.add(Material.NETHER_BRICK);
        //blocks.add(Material.NETHER_FENCE);
        //blocks.add(Material.NETHER_BRICK_STAIRS);
        //blocks.add(Material.NETHER_WARTS);
        //blocks.add(Material.ENCHANTMENT_TABLE);
        //blocks.add(Material.BREWING_STAND);
        //blocks.add(Material.CAULDRON);
        //blocks.add(Material.ENDER_PORTAL);
        //blocks.add(Material.ENDER_PORTAL_FRAME);
        blocks.add(Material.ENDER_STONE);
        //blocks.add(Material.DRAGON_EGG);
        //blocks.add(Material.REDSTONE_LAMP_OFF);
        //blocks.add(Material.WOOD_DOUBLE_STEP);
        //blocks.add(Material.WOOD_STEP);
        //blocks.add(Material.COCOA);
        //blocks.add(Material.SANDSTONE_STAIRS);
        blocks.add(Material.EMERALD_ORE);
        //blocks.add(Material.ENDER_CHEST);
        //blocks.add(Material.TRIPWIRE_HOOK);
        //blocks.add(Material.TRIPWIRE);
        //blocks.add(Material.EMERALD_BLOCK);
        //blocks.add(Material.SPRUCE_WOOD_STAIRS);
        //blocks.add(Material.BIRCH_WOOD_STAIRS);
        //blocks.add(Material.JUNGLE_WOOD_STAIRS);
        //blocks.add(Material.COMMAND);
        //blocks.add(Material.BEACON);
        //blocks.add(Material.COBBLE_WALL);
        //blocks.add(Material.FLOWER_POT);
        //blocks.add(Material.CARROT);
        //blocks.add(Material.POTATO);
        //blocks.add(Material.WOOD_BUTTON);
        //blocks.add(Material.SKULL);
        //blocks.add(Material.ANVIL);
        //blocks.add(Material.TRAPPED_CHEST);
        //blocks.add(Material.GOLD_PLATE);
        //blocks.add(Material.IRON_PLATE);
        //blocks.add(Material.REDSTONE_COMPARATOR_OFF);
        //blocks.add(Material.REDSTONE_COMPARATOR_ON);
        //blocks.add(Material.DAYLIGHT_DETECTOR);
        //blocks.add(Material.REDSTONE_BLOCK);
        blocks.add(Material.QUARTZ_ORE);
        //blocks.add(Material.HOPPER);
        //blocks.add(Material.QUARTZ_BLOCK);
        //blocks.add(Material.QUARTZ_STAIRS);
        //blocks.add(Material.DROPPER);
        blocks.add(Material.STAINED_CLAY);
        //blocks.add(Material.STAINED_GLASS_PANE);
        //blocks.add(Material.LEAVES_2);
        blocks.add(Material.LOG_2);
        //blocks.add(Material.ACACIA_STAIRS);
        //blocks.add(Material.DARK_OAK_STAIRS);
        blocks.add(Material.SLIME_BLOCK);
        //blocks.add(Material.BARRIER);
        //blocks.add(Material.IRON_TRAPDOOR);
        blocks.add(Material.PRISMARINE);
        blocks.add(Material.SEA_LANTERN);
        blocks.add(Material.HAY_BLOCK);
        //blocks.add(Material.CARPET);
        blocks.add(Material.HARD_CLAY);
        //blocks.add(Material.COAL_BLOCK);
        blocks.add(Material.PACKED_ICE);
        //blocks.add(Material.DOUBLE_PLANT);
        //blocks.add(Material.STANDING_BANNER);
        //blocks.add(Material.WALL_BANNER);
        //blocks.add(Material.DAYLIGHT_DETECTOR_INVERTED);
        blocks.add(Material.RED_SANDSTONE);
        //blocks.add(Material.RED_SANDSTONE_STAIRS);
        //blocks.add(Material.DOUBLE_STONE_SLAB2);
        //blocks.add(Material.STONE_SLAB2);
        //blocks.add(Material.SPRUCE_FENCE_GATE);
        //blocks.add(Material.BIRCH_FENCE_GATE);
        //blocks.add(Material.JUNGLE_FENCE_GATE);
        //blocks.add(Material.DARK_OAK_FENCE_GATE);
        //blocks.add(Material.ACACIA_FENCE_GATE);
        //blocks.add(Material.SPRUCE_FENCE);
        //blocks.add(Material.BIRCH_FENCE);
        //blocks.add(Material.JUNGLE_FENCE);
        //blocks.add(Material.DARK_OAK_FENCE);
        //blocks.add(Material.ACACIA_FENCE);
        //blocks.add(Material.SPRUCE_DOOR);
        //blocks.add(Material.BIRCH_DOOR);
        //blocks.add(Material.JUNGLE_DOOR);
        //blocks.add(Material.ACACIA_DOOR);
        //blocks.add(Material.DARK_OAK_DOOR);
        //blocks.add(Material.END_ROD);
        //blocks.add(Material.CHORUS_PLANT);
        //blocks.add(Material.CHORUS_FLOWER);
        blocks.add(Material.PURPUR_BLOCK);
        blocks.add(Material.PURPUR_PILLAR);
        //blocks.add(Material.PURPUR_STAIRS);
        //blocks.add(Material.PURPUR_DOUBLE_SLAB);
        //blocks.add(Material.PURPUR_SLAB);
        blocks.add(Material.END_BRICKS);
        //blocks.add(Material.BEETROOT_BLOCK);
        //blocks.add(Material.GRASS_PATH);
        //blocks.add(Material.END_GATEWAY);
        //blocks.add(Material.COMMAND_REPEATING);
        //blocks.add(Material.COMMAND_CHAIN);
        blocks.add(Material.FROSTED_ICE);
        blocks.add(Material.MAGMA);
        //blocks.add(Material.NETHER_WART_BLOCK);
        blocks.add(Material.RED_NETHER_BRICK);
        //blocks.add(Material.BONE_BLOCK);
        //blocks.add(Material.STRUCTURE_VOID);
        //blocks.add(Material.OBSERVER);
        //blocks.add(Material.WHITE_SHULKER_BOX);
        //blocks.add(Material.ORANGE_SHULKER_BOX);
        //blocks.add(Material.MAGENTA_SHULKER_BOX);
        //blocks.add(Material.LIGHT_BLUE_SHULKER_BOX);
        //blocks.add(Material.YELLOW_SHULKER_BOX);
        //blocks.add(Material.LIME_SHULKER_BOX);
        //blocks.add(Material.PINK_SHULKER_BOX);
        //blocks.add(Material.GRAY_SHULKER_BOX);
        //blocks.add(Material.SILVER_SHULKER_BOX);
        //blocks.add(Material.CYAN_SHULKER_BOX);
        //blocks.add(Material.PURPLE_SHULKER_BOX);
        //blocks.add(Material.BLUE_SHULKER_BOX);
        //blocks.add(Material.BROWN_SHULKER_BOX);
        //blocks.add(Material.GREEN_SHULKER_BOX);
        //blocks.add(Material.RED_SHULKER_BOX);
        //blocks.add(Material.BLACK_SHULKER_BOX);
        blocks.add(Material.WHITE_GLAZED_TERRACOTTA);
        blocks.add(Material.ORANGE_GLAZED_TERRACOTTA);
        blocks.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        blocks.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        blocks.add(Material.YELLOW_GLAZED_TERRACOTTA);
        blocks.add(Material.LIME_GLAZED_TERRACOTTA);
        blocks.add(Material.PINK_GLAZED_TERRACOTTA);
        blocks.add(Material.GRAY_GLAZED_TERRACOTTA);
        blocks.add(Material.SILVER_GLAZED_TERRACOTTA);
        blocks.add(Material.CYAN_GLAZED_TERRACOTTA);
        blocks.add(Material.PURPLE_GLAZED_TERRACOTTA);
        blocks.add(Material.BLUE_GLAZED_TERRACOTTA);
        blocks.add(Material.BROWN_GLAZED_TERRACOTTA);
        blocks.add(Material.GREEN_GLAZED_TERRACOTTA);
        blocks.add(Material.RED_GLAZED_TERRACOTTA);
        blocks.add(Material.BLACK_GLAZED_TERRACOTTA);
        blocks.add(Material.CONCRETE);
        blocks.add(Material.CONCRETE_POWDER);
        //blocks.add(Material.STRUCTURE_BLOCK);

    }

    /*
     private void register() {
        // default blocks
        blocks.add(new InfiniteBlock(Material.STONE));
        blocks.add(new InfiniteBlock(Material.STONE, (byte) 1));
        blocks.add(new InfiniteBlock(Material.STONE, (byte) 3));
        blocks.add(new InfiniteBlock(Material.STONE, (byte) 5));
        blocks.add(new InfiniteBlock(Material.GRASS));
        blocks.add(new InfiniteBlock(Material.DIRT, (byte) 0));
        blocks.add(new InfiniteBlock(Material.DIRT, (byte) 1));
        blocks.add(new InfiniteBlock(Material.DIRT, (byte) 2));
        blocks.add(new InfiniteBlock(Material.COBBLESTONE));
        blocks.add(new InfiniteBlock(Material.SAND, (byte) 0));
        blocks.add(new InfiniteBlock(Material.SAND, (byte) 1));
        blocks.add(new InfiniteBlock(Material.GRAVEL));
        blocks.add(new InfiniteBlock(Material.GOLD_ORE));
        blocks.add(new InfiniteBlock(Material.IRON_ORE));
        blocks.add(new InfiniteBlock(Material.COAL_ORE));
        blocks.add(new InfiniteBlock(Material.LOG, (byte) 0));
        blocks.add(new InfiniteBlock(Material.LOG, (byte) 1));
        blocks.add(new InfiniteBlock(Material.LOG, (byte) 2));
        blocks.add(new InfiniteBlock(Material.LOG, (byte) 3));
        blocks.add(new InfiniteBlock(Material.LAPIS_ORE));
        blocks.add(new InfiniteBlock(Material.SANDSTONE, (byte) 0));
        blocks.add(new InfiniteBlock(Material.SANDSTONE, (byte) 1));
        blocks.add(new InfiniteBlock(Material.SANDSTONE, (byte) 2));
        blocks.add(new InfiniteBlock(Material.WOOL));
        blocks.add(new InfiniteBlock(Material.BRICK));
        blocks.add(new InfiniteBlock(Material.MOSSY_COBBLESTONE));
        blocks.add(new InfiniteBlock(Material.OBSIDIAN));
        blocks.add(new InfiniteBlock(Material.DIAMOND_ORE));
        blocks.add(new InfiniteBlock(Material.REDSTONE_ORE));
        blocks.add(new InfiniteBlock(Material.SNOW_BLOCK));
        blocks.add(new InfiniteBlock(Material.CLAY));
        blocks.add(new InfiniteBlock(Material.PUMPKIN));
        blocks.add(new InfiniteBlock(Material.SMOOTH_BRICK, (byte) 0));
        blocks.add(new InfiniteBlock(Material.SMOOTH_BRICK, (byte) 1));
        blocks.add(new InfiniteBlock(Material.SMOOTH_BRICK, (byte) 2));
        blocks.add(new InfiniteBlock(Material.SMOOTH_BRICK, (byte) 3));
        blocks.add(new InfiniteBlock(Material.HUGE_MUSHROOM_1));
        blocks.add(new InfiniteBlock(Material.HUGE_MUSHROOM_2));
        blocks.add(new InfiniteBlock(Material.MELON_BLOCK));
        blocks.add(new InfiniteBlock(Material.MYCEL));
        blocks.add(new InfiniteBlock(Material.EMERALD_ORE));
        blocks.add(new InfiniteBlock(Material.QUARTZ_ORE));
        blocks.add(new InfiniteBlock(Material.STAINED_CLAY));
        blocks.add(new InfiniteBlock(Material.LOG_2, (byte) 0));
        blocks.add(new InfiniteBlock(Material.LOG_2, (byte) 1));
        blocks.add(new InfiniteBlock(Material.SLIME_BLOCK));
        blocks.add(new InfiniteBlock(Material.HARD_CLAY));
        blocks.add(new InfiniteBlock(Material.PACKED_ICE));
        blocks.add(new InfiniteBlock(Material.RED_SANDSTONE, (byte) 0));
        blocks.add(new InfiniteBlock(Material.RED_SANDSTONE, (byte) 1));
        blocks.add(new InfiniteBlock(Material.RED_SANDSTONE, (byte) 2));
        blocks.add(new InfiniteBlock(Material.MAGMA));

        // ocean blocks
        //blocks.add(new InfiniteBlock(Material.PRISMARINE));
        //blocks.add(new InfiniteBlock(Material.SEA_LANTERN));
        // nether blocks
        //blocks.add(new InfiniteBlock(Material.NETHERRACK));
        //blocks.add(new InfiniteBlock(Material.SOUL_SAND));
        //blocks.add(new InfiniteBlock(Material.GLOWSTONE));
        //blocks.add(new InfiniteBlock(Material.NETHER_BRICK));
        //blocks.add(new InfiniteBlock(Material.RED_NETHER_BRICK));
        // end blocks
        //blocks.add(new InfiniteBlock(Material.ENDER_STONE));
        //blocks.add(new InfiniteBlock(Material.PURPUR_BLOCK));
        //blocks.add(new InfiniteBlock(Material.PURPUR_PILLAR));
        //blocks.add(new InfiniteBlock(Material.END_BRICKS));
    }
     */
    public static int getBlockIDFromSnapshotMap(@NotNull Map<ChunkPos, ChunkSnapshot> snapshots, int absX, int absY, int absZ) {
        int blockChunkX = absX % 16;
        int blockChunkZ = absZ % 16;

        if (blockChunkX < 0) {
            blockChunkX += 16;
        }

        if (blockChunkZ < 0) {
            blockChunkZ += 16;
        }

        ChunkPos pos = new ChunkPos(absX >> 4, absZ >> 4);

        ChunkSnapshot snapshot = snapshots.get(pos);
        if (snapshot == null) {
            return 0;
        }

        return snapshot.getBlockTypeId(blockChunkX, absY, blockChunkZ);
    }

    public static byte convertSignDataToChestData(byte data) {
        switch (data) {
            case 0xC:
                return 0x5;
            case 0x0:
                return 0x3;
            case 0x4:
                return 0x4;
            case 0x8:
                return 0x2;
        }
        System.out.println("Warning, unknown sign post direction:" + data);
        return 0x5;
    }
}
