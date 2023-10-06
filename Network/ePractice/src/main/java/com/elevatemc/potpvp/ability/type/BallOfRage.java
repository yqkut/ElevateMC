package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BallOfRage extends Ability {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.SNOW_BALL;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Ball Of Rage";
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Throw to create a cloud of effects");
        toReturn.add(ChatColor.GRAY + "where all teammates within 5 block");
        toReturn.add(ChatColor.GRAY + "radius will be given Strength II and");
        toReturn.add(ChatColor.GRAY + "Resistance III for 6 seconds.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();

        if (!this.isSimilar(player.getItemInHand())) {
            return;
        }

        event.getEntity().setMetadata("BALL_OF_RAGE", new FixedMetadataValue(PotPvPSI.getInstance(), player.getUniqueId().toString()));

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        if (this.hasCooldown(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    private void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball) || !(event.getEntity().getShooter() instanceof Player) || !event.getEntity().hasMetadata("BALL_OF_RAGE")) {
            return;
        }

        final Projectile snowBall = event.getEntity();
        final Player player = (Player) snowBall.getShooter();

        snowBall.getWorld().createExplosion(snowBall.getLocation(), 0);
        snowBall.getWorld().spigot().playEffect(
                snowBall.getLocation().clone().add(0, 1, 0),
                Effect.EXPLOSION_HUGE
        );

        snowBall.getNearbyEntities(5, 5, 5).stream().filter(it -> it instanceof Player).map(it -> (Player) it).forEach(it -> {

            if (!it.getName().equalsIgnoreCase(player.getName())) {
                return;
            }

            it.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*6, 1), true);
            it.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*6, 2), true);

            it.sendMessage("");
            it.sendMessage(Color.translate("&6You have been hit by &f" + player.getName() + "'s " + this.getDisplayName() + " &6and have been given &fStrength 2 and Resistance 3 &6for 6 seconds!"));
            it.sendMessage("");
        });
    }
}
