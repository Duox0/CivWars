package ru.civwars.handlers;

import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.Config;
import ru.civwars.CivLogger;
import ru.civwars.building.BuildingPlacer;
import ru.civwars.building.BuildError;
import ru.civwars.building.types.BuildingData;
import ru.civwars.building.types.CapitolData;
import ru.civwars.building.validation.StructureValidator;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.chat.requests.ChatRequest;
import ru.civwars.chat.requests.ChatRequestManager;
import ru.civwars.civ.Civilization;
import ru.civwars.civ.CivManager;
import ru.civwars.init.BuildingTypes;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.item.special.ItemFactionFlag;
import ru.civwars.petitions.Petition;
import ru.civwars.petitions.PetitionCiv;
import ru.civwars.petitions.PetitionManager;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.i18n.I18n;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.LocationUtils;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CivHandler {

    public static void handleCivCreate(@NotNull KPlayer player, @NotNull String name, @NotNull String capitalName, int typeId, @NotNull String theme) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_CREATE [{0}]: Name: {1}, Capital: {2}, Type: {3}, Theme: {4}", player.getObjectId(), name, capitalName, typeId, theme);
        }

        if (player.getTown() != null) {
            return;
        }

        // ========================= Checks civilization conditions =========================
        if (name.length() < 3) {
            player.sendError("civ_name_is_too_short_s1", name, 3);
            return;
        } else if (name.length() > 24) {
            player.sendError("civ_name_is_too_long_s1", name, 24);
            return;
        } else if (!(StringUtils.isAlpha(name) && StringUtils.isAsciiPrintable(name))) {
            player.sendError("civ_name_is_invalid", name);
            return;
        } else if (CivManager.getCiv(name) != null) {
            player.sendError("civ_name_is_currently_in_use", name);
            return;
        }

        // ========================= Checks town conditions =========================
        if (capitalName.length() < 3) {
            player.sendError("town_name_is_too_short_s1", capitalName, 3);
            return;
        } else if (name.length() > 24) {
            player.sendError("town_name_is_too_long_s1", capitalName, 24);
            return;
        } else if (!(StringUtils.isAlpha(capitalName) && StringUtils.isAsciiPrintable(capitalName))) {
            player.sendError("town_name_is_invalid", capitalName);
            return;
        } else if (TownManager.getTown(capitalName) != null) {
            player.sendError("town_name_is_currently_in_use", capitalName);
            return;
        }

        // ========================= Checks player conditions =========================
        ItemStack itemstack = player.getHeldItem(BukkitItemSlot.MAIN_HAND);
        CustomItem item = CustomItems.fromItemStack(itemstack);
        if (!(item instanceof ItemFactionFlag)) {
            player.sendError("not_enough_items");
            return;
        }

        // ========================= Checks build conditions =========================
        BuildingData type = BuildingTypes.get(typeId);
        if (!(type instanceof CapitolData)) {
            player.sendError("civ_not_created");
            return;
        }

        CivWorld world = player.getWorld();
        Location location = player.getLocation();
        EnumFacing direction = EnumFacing.fromAngle(location.getYaw());

        Schematic schematic = SchematicManager.getSchematic(type.getSchematicPath(theme, direction));
        if (schematic == null) {
            player.sendError("invalid_schematic");
            return;
        }

        BlockPos corner = LocationUtils.repositionCorner(new BlockPos(location), direction, schematic.getSize().width, schematic.getSize().length);
        corner = new BlockPos(corner.getX(), corner.getY() + schematic.getYShift(), corner.getZ());

        BuildingPlacer placer = new BuildingPlacer(type, schematic);
        BuildError buildResult = placer.canPlaceHere(world, corner.getX(), corner.getY(), corner.getZ());
        if (buildResult != BuildError.OK) {
            player.sendError(buildResult.name());
            return;
        }

        // ========================= Create civilization =========================
        PetitionCiv petition = new PetitionCiv(player.getObjectId(), name, capitalName, (CapitolData) type, world, corner, direction, schematic);
        PetitionManager.addPetition(petition);

        player.startPreviewTask(world, corner, schematic);
        StructureValidator.validate(player, schematic, world, corner, (r) -> {
            if (r.isValid() && player.isOnline()) {
                ChatRequest request = new ChatRequest(player, player, 30) {
                    @Override
                    protected boolean accept(@NotNull KPlayer receiver, @NotNull String[] args) {
                        if (CivHandler.handleCivCreate(player)) {
                            PetitionManager.removePetition(petition);
                            player.stopPreviewTask();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void cancel(@Nullable KPlayer receiver) {
                        PetitionManager.removePetition(petition);
                        if (receiver != null) {
                            receiver.sendError("request_civ_create_cancel");
                            receiver.stopPreviewTask();
                        }
                    }
                };

                ChatRequestManager.instance.sendRequest(request, I18n.tl("request_civ_create_text"), I18n.tl("request_civ_create_tooltip_text", name, capitalName), "/civ accept");
            } else {
                PetitionManager.removePetition(petition);
            }
        });
    }

    private static boolean handleCivCreate(@NotNull KPlayer player) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_CREATE_ACCEPT [{0}]", player.getObjectId());
        }

        if (player.getTown() != null) {
            return true;
        }

        Petition petition = PetitionManager.getPetition(player.getObjectId());
        if (!(petition instanceof PetitionCiv)) {
            return true;
        }

        PetitionCiv petitionCiv = (PetitionCiv) petition;
        String name = petitionCiv.getName();
        String capitalName = petitionCiv.getCapitalName();
        CapitolData type = petitionCiv.getType();
        CivWorld world = petitionCiv.getWorld();
        BlockPos pos = petitionCiv.getPos();
        EnumFacing direction = petitionCiv.getDirection();
        Schematic schematic = petitionCiv.getSchematic();

        if (CivManager.getCiv(name) != null
                || TownManager.getTown(capitalName) != null
                || world == null
                || schematic == null) {
            return true;
        }

        // ========================= Checks player conditions =========================
        ItemStack itemstack = player.getHeldItem(BukkitItemSlot.MAIN_HAND);
        CustomItem item = CustomItems.fromItemStack(itemstack);
        if (!(item instanceof ItemFactionFlag)) {
            player.sendError("not_enough_items");
            return false;
        }

        // ========================= Checks build conditions =========================
        BuildingPlacer placer = new BuildingPlacer(type, schematic);
        BuildError buildResult = placer.canPlaceHere(world, pos.getX(), pos.getY(), pos.getZ());
        if (buildResult != BuildError.OK) {
            player.sendError(buildResult.name());
            return true;
        }

        // ========================= Create civilization =========================
        player.setHeldItem(BukkitItemSlot.MAIN_HAND, new ItemStack(Material.AIR));

        Civilization civ = new Civilization(UUID.randomUUID());
        if (!civ.create(player, name)) {
            player.sendError("civ_not_created");
            return true;
        }

        Town town = new Town(UUID.randomUUID());
        civ.addTown(town);

        if (!town.create(player, civ, capitalName, true, type, world, pos, direction, schematic)) {
            player.sendError("civ_not_created");
            civ.disband();
            return true;
        }

        civ.setCapitalId(town);

        Bukkit.broadcastMessage(I18n.tl("civ_created", player.getName(), name, capitalName));

        CivManager.addCiv(civ);
        TownManager.addTown(town);
        return true;
    }

    public static void handleCivChangeName(@NotNull KPlayer player, @NotNull String name) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_CHANGE_NAME [{0}]: Name: {1}", player.getObjectId(), name);
        }
        
        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleChangeName(player, name);
        }
    }

    public static void handleCivChangeLeader(@NotNull KPlayer player, @NotNull String newLeaderName) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_CHANGE_LEADER [{0}]: Target: {1}", player.getObjectId(), newLeaderName);
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleChangeLeader(player, newLeaderName);
        }
    }

    public static void handleCivClaimLeader(@NotNull KPlayer player) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_CLAIM_LEADER [{0}]", player.getObjectId());
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleClaimLeader(player);
        }
    }

    public static void handleCivMod(@NotNull KPlayer player, @NotNull String newModName) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_MOD [{0}]: Target: {1}", player.getObjectId(), newModName);
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleMod(player, newModName);
        }
    }

    public static void handleCivUnmod(@NotNull KPlayer player, @NotNull String modName) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_UNMOD [{0}]: Target: {1}", player.getObjectId(), modName);
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleUnmod(player, modName);
        }
    }

    public static void handleCivDepositGold(@NotNull KPlayer player, long money) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_DEPOSIT_MONEY [{0}]: Money: {1}", player.getObjectId(), money);
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleMemberDepositGold(player, money);
        }
    }

    public static void handleCivWithdrawGold(@NotNull KPlayer player, long money) {
        if (Config.DEBUG) {
            CivLogger.info("CIV_WITHDRAW_MONEY [{0}]: Money: {1}", player.getObjectId(), money);
        }

        Civilization civ = player.getCiv();
        if (civ != null) {
            civ.handleMemberWithdrawGold(player, money);
        }
    }
}
