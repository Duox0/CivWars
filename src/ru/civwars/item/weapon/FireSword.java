package ru.civwars.item.weapon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.civwars.item.CustomItem;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.EnumInteractionResult;
import ru.civwars.util.InteractionResult;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class FireSword extends ItemWeapon {

    public FireSword(@NotNull CustomItem.Property property, double attackDamage) {
        super(property, attackDamage);
    }

    @NotNull
    @Override
    public InteractionResult<ItemStack> useItemRightClick(@NotNull ItemStack stack, @NotNull KPlayer player) {
        Location location = player.getLocation();
        location.getWorld().getNearbyEntities(location, 5.0D, 5.0D, 5.0D).stream()
                .filter(e -> ((e instanceof LivingEntity) && e != player.getEntity()))
                .forEach(e -> {
                    double motionX = 1.0;
                    double motionY = 1.0;
                    double motionZ = 1.0;

                    Location el = e.getLocation();
                    double d1 = location.getX() - el.getX();
                    double d2 = location.getZ() - el.getZ();
                    for (; d1 * d1 + d2 * d2 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01, d2 = (Math.random() - Math.random()) * 0.01) {
                    }
                    final double f = Math.sqrt(d1 * d1 + d2 * d2);
                    motionX /= 2.0;
                    motionZ /= 2.0;
                    motionX -= d1 / f * 1.0;
                    motionZ -= d2 / f * 1.0;
                    if (e.isOnGround()) {
                        motionY /= 2.0;
                        motionY += 1.0;
                        if (motionY > 0.4000000059604645) {
                            motionY = 0.4000000059604645;
                        }
                    }
                    e.setFireTicks(5 * 20);
                    e.setVelocity(new Vector(motionX, motionY, motionZ));
                });

        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 4.0f, (1.0f + 1.2f) * 0.7f);
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
        
        ItemUtils.damageItem(stack, 1, new Random(), player.getEntity());
        return new InteractionResult<ItemStack>(EnumInteractionResult.SUCCESS, stack);
    }
    
    @NotNull
    @Override
    public InteractionResult<ItemStack> useItemRightClickBlock(@NotNull ItemStack stack, @NotNull KPlayer player, @NotNull Block block, @NotNull BlockFace facing) {
        if(!(block.getType() == Material.GRASS && facing == BlockFace.UP)) {
            return new InteractionResult<ItemStack>(EnumInteractionResult.PASS, stack);
        }
        
        block.setType(Material.AIR);
        ItemUtils.damageItem(stack, 1, new Random(), player.getEntity());
        return new InteractionResult<ItemStack>(EnumInteractionResult.SUCCESS, stack);
    }

    public static class Serializer extends ItemWeapon.Serializer<FireSword> {

        public Serializer() {
            super("test_sword", FireSword.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull FireSword item, @NotNull JsonSerializationContext context) {
            super.serialize(object, item, context);
        }

        @NotNull
        @Override
        public FireSword deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double attackDamage) {
            return new FireSword(property, attackDamage);
        }
    }

}
