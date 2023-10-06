package com.elevatemc.ehub.listener;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.elib.util.TaskUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerListener implements Listener {
    private final eHub plugin = eHub.getInstance();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());
        plugin.getRedisManager().load(profile);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (ItemStack stack : player.getInventory().getArmorContents()) {
            stack.setType(Material.AIR);
        }

        player.updateInventory();
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        TaskUtil.runTaskAsynchronously(() -> {
            Profile profile = plugin.getProfileManager().getByUuid(event.getPlayer().getUniqueId());

            plugin.getRedisManager().saveArmor(profile, profile.getArmorType());
            plugin.getRedisManager().saveParticle(profile, profile.getParticleType());
            plugin.getProfileManager().getProfiles().remove(profile.getUuid());
        });
    }
}
