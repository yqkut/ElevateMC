package com.elevatemc.potpvp.pvpclasses.pvpclasses;

import com.elevatemc.potpvp.pvpclasses.PvPClass;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import com.elevatemc.elib.util.TimeUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class RogueClass extends PvPClass {

    private static Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static Map<String, Long> lastJumpUsage = new HashMap<>();
    private static Map<String, Long> backstabCooldown = new HashMap<>();

    private static PotionEffect PERMANENT_SPEED_THREE = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
    private static PotionEffect PERMANENT_JUMP = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1);
    private static PotionEffect PERMANENT_RESISTANCE = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0);
    private static PotionEffect SPEED_FIVE = new PotionEffect(PotionEffectType.SPEED, 200, 4);
    private static PotionEffect JUMP = new PotionEffect(PotionEffectType.JUMP, 200, 6);
    private static PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 2 * 20, 2);

    public RogueClass() {
        super("Rogue", 15, "CHAINMAIL_", Arrays.asList(Material.SUGAR, Material.FEATHER));
    }
    @Override
    public void apply(Player player) {
        player.addPotionEffect(PERMANENT_SPEED_THREE, true);
        player.addPotionEffect(PERMANENT_JUMP, true);
        player.addPotionEffect(PERMANENT_RESISTANCE, true);
    }

    @Override
    public void tick(Player player) {
        if (!(this.qualifies(player.getInventory()))) {
            super.tick(player);
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(PERMANENT_SPEED_THREE);
        }

        if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
            player.addPotionEffect(PERMANENT_JUMP);
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(PERMANENT_RESISTANCE);
        }
    }

    @Override
    public void remove(Player player) {
        removeInfiniteEffects(player);
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = ((lastSpeedUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L) * 1000L;
                String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + (1000L * 60 * 2));
            player.addPotionEffect(SPEED_FIVE, true);

        } else {
            if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = ((lastJumpUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L) * 1000L;
                String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + (1000L * 60 * 2));
            player.addPotionEffect(JUMP, true);
        }

        return (true);
    }

    @EventHandler
    public void onPotionEffectExpire(PotionEffectExpireEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (PvPClassHandler.hasKitOn(player, this)) {
                if (e.getEffect().getType().equals(PotionEffectType.SPEED)) {
                    player.addPotionEffect(PERMANENT_SPEED_THREE, true);
                }
                if (e.getEffect().getType().equals(PotionEffectType.JUMP)) {
                    player.addPotionEffect(PERMANENT_JUMP, true);
                }
                if (e.getEffect().getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                    player.addPotionEffect(PERMANENT_RESISTANCE, true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (damager.getItemInHand() != null && damager.getItemInHand().getType() == Material.GOLD_SWORD && PvPClassHandler.hasKitOn(damager, this)) {
                if (backstabCooldown.containsKey(damager.getName()) && backstabCooldown.get(damager.getName()) > System.currentTimeMillis()) {
                    return;
                }

                backstabCooldown.put(damager.getName(), System.currentTimeMillis() + 1500L);

                org.bukkit.util.Vector playerVector = damager.getLocation().getDirection();
                Vector entityVector = victim.getLocation().getDirection();

                playerVector.setY(0F);
                entityVector.setY(0F);

                double degrees = playerVector.angle(entityVector);

                if (Math.abs(degrees) < 1.4) {
                    damager.setItemInHand(new ItemStack(Material.AIR));

                    damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
                    damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

                    if (victim.getHealth() - 7D <= 0) {
                        event.setCancelled(true);
                    } else {
                        event.setDamage(0D);
                    }

                    victim.setHealth(Math.max(0D, victim.getHealth() - 7D));

                    damager.addPotionEffect(SLOW);
                } else {
                    damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Backstab failed!");
                }
            }
        }
    }
}
