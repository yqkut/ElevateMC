package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SoulStone extends Ability {

    @Getter
    private Map<UUID,LastDamageEntry> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_INGOT;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Soul Stone";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Swap positions with the last");
        toReturn.add(ChatColor.GRAY + "person who hit you within 5 seconds.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final long difference = TimeUnit.SECONDS.toMillis(5L);

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
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No player has hit you within the last 5 seconds.");
            return;
        }
        final LastDamageEntry entry = cache.get(player.getUniqueId());

        final Player target = PotPvPSI.getInstance().getServer().getPlayer(entry.getUuid());

        AntiBlockup.getCache().put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            AntiBlockup.cache.remove(event.getPlayer().getUniqueId());

            event.getPlayer().sendMessage("");
            event.getPlayer().sendMessage(Color.translate("&cThe &f" + this.getDisplayName() + " &chas expired! You may now place blocks!"));
            event.getPlayer().sendMessage("");
        }, 20*10);

        event.getPlayer().sendMessage(Color.translate("&cYou have been put on the &6&lAnti-Blockup &cfor 10 seconds as you used the &f" + getDisplayName() + "&c!"));

        new BukkitRunnable() {
            private int seconds = 3;

            @Override
            public void run() {

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }
                final Player target = PotPvPSI.getInstance().getServer().getPlayer(entry.getUuid());

                if (this.seconds < 1) {

                    if (PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player) == null) {
                        this.cancel();
                        return;
                    }

                    final Location location = (target != null && target.isOnline()) ? target.getLocation():entry.getLocation();

                    if (target != null && target.isOnline()) {
                        target.teleport(event.getPlayer().getLocation());
                    }

                    event.getPlayer().teleport(location);

                    this.cancel();
                    return;
                }

                this.seconds--;

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Swapping positions with " + ChatColor.WHITE + UUIDUtils.name(entry.getUuid()) + ChatColor.YELLOW + " in " + ChatColor.RED + (this.seconds+1) + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");

                if (target != null && target.isOnline()) {
                    target.sendMessage(PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + ChatColor.RED + " used " + getDisplayName() + ChatColor.RED + " and will swap positions with you in " + ChatColor.WHITE + (this.seconds+1) + ChatColor.RED + " second" + (this.seconds == 1 ? "":"s") + ".");
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