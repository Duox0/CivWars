package ru.civwars.world;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import ru.civwars.CivWars;
import ru.civwars.building.Building;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.entity.BasicEntity;
import ru.civwars.entity.NPCEntity;
import ru.civwars.entity.MobEntity;
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.building.instance.Structure;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.town.CultureChunk;
import ru.civwars.util.BlockPos;
import ru.civwars.util.ChunkCoord;
import ru.civwars.util.ChunkPos;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CivWorld {

    private final World world;

    private final ConcurrentHashMap<UUID, Building> buildings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BlockPos, BuildingBlock> buildingBlocks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<ChunkPos, Building> buildingInChunk = new ConcurrentHashMap<>();
    
    private final ConcurrentHashMap<ChunkCoord, CultureChunk> cultureChunks = new ConcurrentHashMap<>();

    private final Map<UUID, BasicEntity> entities = Maps.newConcurrentMap();

    public CivWorld(@NotNull World world) {
        this.world = world;
    }

    @NotNull
    public final UUID getId() {
        return this.world.getUID();
    }

    @NotNull
    public final String getName() {
        return this.world.getName();
    }

    @NotNull
    public final World getWorld() {
        return this.world;
    }

    /**
     * Добавляет структуру.
     *
     * @param building структура.
     */
    public void addStructure(@NotNull Building building) {
        if (this.buildings.containsKey(building.getObjectId())) {
            return;
        }
        this.buildings.put(building.getObjectId(), building);

        for (ChunkPos chunk : building.getBusyChunks()) {
            if (this.buildingInChunk.containsKey(chunk)) {
                continue;
            }
            this.buildingInChunk.put(chunk, building);
        }
    }

    /**
     * Удаляет структуру.
     *
     * @param structure структура.
     */
    public void removeStructure(@NotNull Building structure) {
        if (!this.buildings.containsKey(structure.getObjectId())) {
            return;
        }
        this.buildings.remove(structure.getObjectId());

        for (ChunkPos chunk : structure.getBusyChunks()) {
            if (!this.buildingInChunk.containsKey(chunk)) {
                continue;
            }
            this.buildingInChunk.remove(chunk);
        }
    }

    /**
     * @param structureId - главный идетификатор структуры.
     * @return структура или null, если структура с данным идентификатором не
     * найдена.
     */
    @Nullable
    public Building getStructure(@NotNull UUID structureId) {
        return this.buildings.get(structureId);
    }

    /**
     * @return список всех структур.
     */
    @NotNull
    public Collection<Building> getStructures() {
        return this.buildings.values();
    }

    /**
     * @param x
     * @param z
     * @return структура, {@code null} если не найдена.
     */
    @Nullable
    public Building getBuildingAtChunk(int x, int z) {
        return this.buildingInChunk.get(new ChunkPos(x, z));
    }

    /**
     * @param chunk
     * @return структура, {@code null} если не найдена.
     */
    @Nullable
    public Building getBuildingAtChunk(@NotNull Chunk chunk) {
        return this.getBuildingAtChunk(chunk.getX(), chunk.getZ());
    }

    /**
     * Добавляет структурный блок.
     *
     * @param block - структурный блок, который нужно добавить.
     */
    public void addBuildingBlock(@NotNull BuildingBlock block) {
        if (this.buildingBlocks.containsKey(block.getPosition())) {
            return;
        }
        this.buildingBlocks.put(block.getPosition(), block);
    }

    /**
     * Удаляет структурный блок.
     *
     * @param block - структурный блок, который нужно удалить.
     */
    public void removeBuildingBlock(@NotNull BuildingBlock block) {
        if (!this.buildingBlocks.containsKey(block.getPosition())) {
            return;
        }
        this.buildingBlocks.remove(block.getPosition());
    }

    /**
     * @param pos - позиция в мире.
     * @return структурный блок или null, если структурный блок на данных
     * координатах не найден.
     */
    @Nullable
    public BuildingBlock getBuildingBlock(@NotNull BlockPos pos) {
        return this.buildingBlocks.get(pos);
    }

    /**
     * @return список всех структурных блоков.
     */
    @NotNull
    public Collection<BuildingBlock> getBuildingBlocks() {
        return this.buildingBlocks.values();
    }
    
    /**
     * Добавляет культурный чанк.
     *
     * @param cultureChunk - культурный чанк, который нужно добавить.
     */
    public void addCultureChunk(@NotNull CultureChunk cultureChunk) {
        if (this.cultureChunks.containsKey(cultureChunk.getCoord())) {
            return;
        }
        this.cultureChunks.put(cultureChunk.getCoord(), cultureChunk);
    }

    /**
     * Удаляет культурный чанк.
     *
     * @param cultureChunk - культурный чанк, который нужно удалить.
     */
    public void removeCultureChunk(@NotNull CultureChunk cultureChunk) {
        if (!this.cultureChunks.containsKey(cultureChunk.getCoord())) {
            return;
        }
        this.cultureChunks.remove(cultureChunk.getCoord());
    }

    /**
     * @param coord - координаты культурного чанка в мире.
     * @return культурный чанк, или {@code null} если не найден.
     */
    @Nullable
    public CultureChunk getCultureChunk(@NotNull ChunkCoord coord) {
        return this.cultureChunks.get(coord);
    }

    /**
     * @return список всех культурных чанков.
     */
    @NotNull
    public Collection<CultureChunk> getCultureChunks() {
        return this.cultureChunks.values();
    }

    /**
     * Регистрирует новую сущность.
     *
     * @param entity - сущность.
     */
    public void registerEntity(@NotNull BasicEntity entity) {
        if (!this.entities.containsKey(entity.getObjectId())) {
            this.entities.put(entity.getObjectId(), entity);
        }
    }

    /**
     * Отменяет регистрацию сущности.
     *
     * @param entity - сущность.
     */
    public void unregisterEntity(@NotNull BasicEntity entity) {
        if (this.entities.containsKey(entity.getObjectId())) {
            this.entities.remove(entity.getObjectId());
        }
    }

    /**
     * Получает моба, в случает отсутствия создает нового и регистрирует его.
     *
     * @param entity
     * @return BasicEntity.
     */
    @NotNull
    public BasicEntity getEntity(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return PlayerManager.getPlayer((Player) entity);
        }
        BasicEntity mob = this.entities.get(entity.getUniqueId());
        if (mob == null) {
            mob = new NPCEntity(entity);
            this.entities.put(mob.getObjectId(), mob);
        }

        return mob;
    }

    @NotNull
    public Entity spawn(@NotNull NPCTemplate template, double x, double y, double z) {
        Location location = new Location(this.world, x, y, z);
        Entity entity = template.getEntityType().spawn(location);

        MobEntity mob = new MobEntity(entity, template);

        if (entity instanceof LivingEntity) {
            LivingEntity asLiving = (LivingEntity) entity;

            double health = template.getBaseHealth();
            if (health > 0.0) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                    asLiving.setHealth(health);
                } catch (IllegalArgumentException ex) {
                    CivWars.get().getLogger().info("Mob HP is greater than server's maxHealth setting. Please modify spigot.yml and increase the maxHealth attribute to compensate.");
                    ex.printStackTrace();
                }
            }

            double defense = template.getBaseDefense();
            if (defense >= 0.0) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(defense);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double defenseToughness = template.getBaseDefenseToughness();
            if (defenseToughness >= 0.0 && asLiving.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(defenseToughness);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double attackDamage = template.getBaseAttackDamage();
            if (attackDamage > 0.0 && asLiving.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double attackSpeed = template.getBaseAttackSpeed();
            if (attackDamage > 0.0 && asLiving.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double movementSpeed = template.getBaseMovementSpeed();
            if (movementSpeed > 0.0 && asLiving.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double flyingSpeed = template.getBaseFlyingSpeed();
            if (flyingSpeed > 0.0 && asLiving.getAttribute(Attribute.GENERIC_FLYING_SPEED) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(flyingSpeed);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double knockbackResist = template.getBaseFlyingSpeed();
            if (knockbackResist > 0.0 && asLiving.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResist);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            double followRange = template.getBaseFollowRange();
            if (followRange > 0.0 && asLiving.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null) {
                try {
                    asLiving.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(followRange);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

        }

        entity.setMetadata("custom_mob", new FixedMetadataValue(CivWars.get(), true));
        entity.setMetadata("mob_id", new FixedMetadataValue(CivWars.get(), template.getId()));

        this.registerEntity(mob);

        return entity;
    }

    public void load() {
    }

    public void completeSetup() {
        this.buildings.values().forEach(s -> {
            s.isValidated();
            //if (!s.isCompleted()) {
            //    s.startBuildTask();
            //}
        });
    }

    public void tick() {
        for (Building b : this.buildings.values()) {
            try {
                b.tick();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        
        for (BasicEntity entity : this.entities.values()) {
            entity.tick();
        }
    }
}
