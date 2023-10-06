package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NinjaStar extends Ability {
    @Getter
    private Map<UUID,LastDamageEntry> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.NETHER_STAR;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Ninja Star";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Teleport to the last");
        toReturn.add(ChatColor.GRAY + "person who hit you within 30 seconds.");
        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final long difference = TimeUnit.SECONDS.toMillis(30L);


        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (player.getItemInHand() == null || !this.isSimilar(player.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (!this.cache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - this.cache.get(player.getUniqueId()).getTime()) > difference) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No player has hit you within the last 30 seconds.");
            return;
        }
        final LastDamageEntry entry = cache.get(player.getUniqueId());

        final Player target = PotPvPSI.getInstance().getServer().getPlayer(entry.getUuid());

        if (target.isOnline()) {

            target.setMetadata("NINJASTAR", new FixedMetadataValue(PotPvPSI.getInstance(), true));
            target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use an enderpearl or timewarp for the next 5 seconds...");

            PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
                target.removeMetadata("NINJASTAR", PotPvPSI.getInstance());
            }, 20*5);
        }

        new BukkitRunnable() {
            private int seconds = 3;

            @Override
            public void run() {

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                if (PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player) == null) {
                    this.cancel();
                    return;
                }

                final Player target = PotPvPSI.getInstance().getServer().getPlayer(entry.getUuid());

                if (target == null && !target.isOnline()) {
                    this.cancel();
                    return;
                }


                if (this.seconds < 1) {

                    final Location location = (target != null && target.isOnline()) ? target.getLocation():entry.getLocation();

                    event.getPlayer().teleport(location);

                    this.cancel();
                    return;
                }

                this.seconds--;

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Teleporting to " + ChatColor.WHITE + UUIDUtils.name(entry.getUuid()) + ChatColor.YELLOW + " in " + ChatColor.RED + (this.seconds+1) + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");

                if (target != null && target.isOnline()) {
                    target.sendMessage(PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + ChatColor.RED + " will teleport to you in " + ChatColor.WHITE + (this.seconds+1) + ChatColor.RED + " second" + (this.seconds == 1 ? "":"s") + ".");
              }
            }

        }.runTaskTimer(PotPvPSI.getInstance(),0L,20L);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getEntity().getShooter();

        if (shooter.hasMetadata("NINJASTAR")) {
            event.setCancelled(true);
            shooter.updateInventory();
            shooter.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not throw enderpearls whilst someone is using a " + this.getDisplayName() + ChatColor.RED + " on you!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        this.cache.put(event.getEntity().getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),event.getDamager().getUniqueId(),event.getDamager().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {

        if (event.isCancelled() || event.getDamager() instanceof EnderPearl) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Projectile)) {
            return;
        }

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        final Player damager = (Player) ((Projectile) event.getDamager()).getShooter();

        this.cache.put(event.getEntity().getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),damager.getUniqueId(),damager.getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        this.cache.remove(event.getEntity().getUniqueId());

        final Optional<Map.Entry<UUID, LastDamageEntry>> optionalLastDamageEntry = this.cache.entrySet().stream().filter(it -> it.getValue().getUuid().toString().equalsIgnoreCase(event.getEntity().getUniqueId().toString())).findFirst();

        if (!optionalLastDamageEntry.isPresent()) {
            return;
        }

        this.cache.remove(optionalLastDamageEntry.get().getKey(), optionalLastDamageEntry.get().getValue());
    }

    @AllArgsConstructor
    static class LastDamageEntry {

        @Getter
        private long time;
        @Getter
        private UUID uuid;
        @Getter
        private Location location;

    }
}