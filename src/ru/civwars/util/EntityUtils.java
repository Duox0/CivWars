package ru.civwars.util;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import ru.lib27.annotation.NotNull;

public class EntityUtils {

    private static final Random RND = new Random();

    public static void renderBrokenItemStack(@NotNull LivingEntity entity, @NotNull ItemStack stack) {
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 0.8f + RND.nextFloat() * 0.4f);
        for (int i = 0; i < 5; ++i) {
            entity.getWorld().spawnParticle(Particle.ITEM_CRACK, entity.getLocation(), i);
            
            Location loc = entity.getLocation();
            
            Location vec3d = new Location(entity.getWorld(), (RND.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d.setPitch(-entity.getLocation().getPitch() * 0.017453292f);
            vec3d.setY(-entity.getLocation().getYaw()* 0.017453292f);
            final double d0 = -RND.nextFloat() * 0.6 - 0.3;
            Location vec3d2 = new Location(entity.getWorld(), (RND.nextFloat() - 0.5) * 0.3, d0, 0.6);
            vec3d2.setPitch(-loc.getPitch() * 0.017453292f);
            vec3d2.setYaw(-loc.getYaw() * 0.017453292f);
            vec3d2.add(loc.getX(), loc.getY() + entity.getEyeHeight(), loc.getZ());
            entity.getWorld().spawnParticle(Particle.ITEM_CRACK, vec3d2.getX(), vec3d2.getY(), vec3d2.getZ(), 0, vec3d.getX(), vec3d.getY() + 0.05, vec3d.getZ(), 0.0, stack.getTypeId());
        }
    }

}
