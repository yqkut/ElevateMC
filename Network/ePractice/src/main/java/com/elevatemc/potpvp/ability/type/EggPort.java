package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EggPort extends Ability {

    public static final int SWAP_RADIUS = 15;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.EGG;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Eggport";
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Switch places with your");
        toReturn.add(ChatColor.GRAY + "enemy that are within 15 blocks!");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 10_000L;
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Egg) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();

        if (!this.isSimilar(player.getItemInHand())) {
            return;
        }

        event.getEntity().setMetadata("Eggport", new FixedMetadataValue(PotPvPSI.getInstance(), player.getUniqueId().toString()));

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        final Player player = event.getPlayer();

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreature(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Egg) || !event.getDamager().hasMetadata("Eggport")) {
            return;
        }

        final Player shooter = PotPvPSI.getInstance().getServer().getPlayer(UUID.fromString(event.getDamager().getMetadata("Eggport").get(0).asString()));
        final Player target = (Player) event.getEntity();

        if (shooter == null) {
            return;
        }

        final Location shooterLocation = shooter.getLocation().clone();
        final Location targetLocation = target.getLocation().clone();

        if (shooterLocation.distance(targetLocation) > SWAP_RADIUS) {
            shooter.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need to be within 15 blocks of that player!");
            return;
        }

        shooter.teleport(targetLocation);
        target.teleport(shooterLocation);

        shooter.sendMessage(ChatColor.GREEN + "Poof! You hit " + target.getName() + " with your Eggport.");
        target.sendMessage(ChatColor.GREEN + "Poof! You were hit by a Eggport!");

        this.removeCooldown(shooter);
        this.applyCooldown(shooter, 20_000L);
    }
}