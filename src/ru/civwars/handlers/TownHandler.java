package ru.civwars.handlers;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.Config;
import ru.civwars.CivLogger;
import ru.civwars.building.types.BuildingData;
import ru.civwars.building.types.TownHallData;
import ru.civwars.building.validation.StructureValidator;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.chat.Chat;
import ru.civwars.chat.requests.ChatRequest;
import ru.civwars.chat.requests.ChatRequestManager;
import ru.civwars.init.BuildingTypes;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.item.special.ItemSettlement;
import ru.civwars.network.SystemMessage;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.LocationUtils;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class TownHandler {

    public static class TownBuilder {

        public String name;
        public int typeId;
        public UUID worldId;
        public int tileX;
        public int tileZ;
        public int height;
        public EnumFacing direction;
        public String schematic;
    }

    private static final Map<UUID, TownBuilder> TOWN_BUILDERS = Maps.newHashMap();

    public static void handleTownCreate(@NotNull KPlayer player, @NotNull String name, int typeId) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN PREPARE CREATE [{0}]: Name: {1}, Type: {2}", player.getName(), name, typeId);
        }

        BuildingData type = BuildingTypes.get(typeId);
        if (!(type instanceof TownHallData)) {
            player.sendError(SystemMessage.TOWN_NOT_CREATED, name);
            return;
        }

        CustomItem item = CustomItems.fromItemStack(player.getHeldItem(BukkitItemSlot.MAIN_HAND));
        if (!(item instanceof ItemSettlement)) {
            player.sendError(SystemMessage.TOWN_NOT_ENOUGH_ITEM, name);
            return;
        }

        CivWorld world = player.getWorld();
        Location location = player.getLocation();
        EnumFacing direction = EnumFacing.fromAngle(location.getYaw());

        Schematic schematic = SchematicManager.getSchematic(type.getSchematicPath("default", direction));
        if (schematic == null) {
            return;
        }

        BlockPos corner = LocationUtils.repositionCorner(new BlockPos(location), direction, schematic.getSize().width, schematic.getSize().length);
        corner = new BlockPos(corner.getX(), corner.getY() + schematic.getYShift(), corner.getZ());

        TownBuilder builder = new TownBuilder();
        builder.name = name;
        builder.typeId = typeId;
        builder.worldId = world.getId();
        builder.tileX = corner.getX() >> 4;
        builder.tileZ = corner.getZ() >> 4;
        builder.height = corner.getY();
        builder.direction = direction;
        builder.schematic = schematic.getFilepath();
        TOWN_BUILDERS.put(player.getObjectId(), builder);

        player.startPreviewTask(world, corner, schematic);
        StructureValidator.validate(player, schematic, world, corner, (r) -> {
            if (r.isValid() && player.isOnline()) {
                ChatRequest request = new ChatRequest(player, player, 30) {
                    @Override
                    protected boolean accept(@NotNull KPlayer receiver, @NotNull String[] args) {
                        if (TownHandler.handleTownCreate(player)) {
                            TOWN_BUILDERS.remove(player.getObjectId());
                            player.stopPreviewTask();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void cancel(@Nullable KPlayer receiver) {
                        player.sendError("message_creation_town_has_been_cancelled");
                        TOWN_BUILDERS.remove(player.getObjectId());
                        player.stopPreviewTask();
                    }
                };

                player.sendMessage(Chat.buildTitle("build_town_title"));
                player.sendMessage("new_town_promt");
                player.sendMessage("new_town_requirements");
                ChatRequestManager.instance.sendRequest(request, "new_town_button", "new_town_button_hover", "/town accept ");
            } else {
                TOWN_BUILDERS.remove(player.getObjectId());
            }
        });
    }

    private static boolean handleTownCreate(@NotNull KPlayer player) {
        TownBuilder builder = TOWN_BUILDERS.get(player.getObjectId());
        if (builder == null) {
            return true;
        }

        String name = builder.name;
        BuildingData type = BuildingTypes.get(builder.typeId);
        CivWorld world = WorldManager.getWorld(builder.worldId);
        int tileX = builder.tileX;
        int tileZ = builder.tileZ;
        int height = builder.height;
        EnumFacing direction = builder.direction;
        Schematic schematic = SchematicManager.getSchematic(builder.schematic, false);

        if (!(type instanceof TownHallData) || world == null || schematic == null) {
            return true;
        }

        if (Config.DEBUG) {
            CivLogger.info("TOWN CREATE [{0}]: Name: {1}, Type: {2}, Tile: ({4},{5}), Height: {6}, Direction: {7}, Schematic: {8}",
                    player.getName(), name, type.getId(), tileX, tileZ, height, direction.getName(), schematic.getFilepath());
        }

        CustomItem item = CustomItems.fromItemStack(player.getHeldItem(BukkitItemSlot.MAIN_HAND));
        if (!(item instanceof ItemSettlement)) {
            player.sendError(SystemMessage.TOWN_NOT_ENOUGH_ITEM, name);
            return false;
        }

        if (player.getTown() != null) {
            return true;
        }

        Town town = new Town(UUID.randomUUID());

        player.sendMessage("message_creating_town");
        //if (!town.create(player, name, (TownHallData) type, world, tileX, tileZ, height, direction, schematic)) {
        //    player.sendError(SystemMessage.TOWN_NOT_CREATED);
        //    return true;
        //}

        player.setHeldItem(BukkitItemSlot.MAIN_HAND, new ItemStack(Material.AIR));
        player.sendError(SystemMessage.TOWN_CREATE, name);
        return true;
    }

    public static void handleTownChangeName(@NotNull KPlayer player, @NotNull String name) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_CHANGE_NAME [{0}]: Name: {1}", player.getObjectId(), name);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleChangeName(player, name);
        }
    }

    public static void handleTownChangeLeader(@NotNull KPlayer player, @NotNull String newLeaderName) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_CHANGE_LEADER [{0}]: Target: {1}", player.getObjectId(), newLeaderName);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleChangeLeader(player, newLeaderName);
        }
    }

    public static void handleTownClaimLeader(@NotNull KPlayer player) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_CLAIM_LEADER [{0}]", player.getObjectId());
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleClaimLeader(player);
        }
    }
    
    public static void handleTownInviteMember(@NotNull KPlayer player, @NotNull String playername) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_INVITE_MEMBER [{0}]: Target: {1}", player.getObjectId(), playername);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleInviteMember(player, playername);
        }
    }

    public static void handleTownInviteAccept(@NotNull KPlayer player) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_INVITE_ACCEPT [{0}]", player.getObjectId());
        }

        if (player.getTown() == null) {
            if (player.getTownInvited() != null) {
                Town town = TownManager.getTown(player.getTownInvited());
                if (town != null) {
                    town.handleAcceptMember(player);
                }
            }
        }
    }

    public static void handleTownLeaveMember(@NotNull KPlayer player) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_LEAVE_MEMBER [{0}]", player.getObjectId());
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleLeaveMember(player);
        }
    }

    public static void handleTownKickMember(@NotNull KPlayer player, @NotNull String playername) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_KICK_MEMBER [{0}]: Target: {1}", player.getObjectId(), playername);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleKickMember(player, playername);
        }
    }

    public static void handleTownPromote(@NotNull KPlayer player, @NotNull String playername) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_PROMOTE [{0}]: Target: {1}", player.getObjectId(), playername);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleUpdateMemberRank(player, playername, false);
        }
    }

    public static void handleTownDemote(@NotNull KPlayer player, @NotNull String playername) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_DEMOTE [{0}]: Target: {1}", player.getObjectId(), playername);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleUpdateMemberRank(player, playername, true);
        }
    }

    public static void handleTownDepositGold(@NotNull KPlayer player, long money) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_DEPOSIT_MONEY [{0}]: Money: {1}", player.getObjectId(), money);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleMemberDepositGold(player, money);
        }
    }

    public static void handleTownWithdrawGold(@NotNull KPlayer player, long money) {
        if (Config.DEBUG) {
            CivLogger.info("TOWN_WITHDRAW_MONEY [{0}]: Money: {1}", player.getObjectId(), money);
        }

        Town town = player.getTown();
        if (town != null) {
            town.handleMemberWithdrawGold(player, money);
        }
    }

}
