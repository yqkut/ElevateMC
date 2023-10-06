package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class TimeStone extends Ability {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.EMERALD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Time Stone";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Right Click to reset all of your");
        toReturn.add(ChatColor.GRAY + "ability/partner item cooldowns.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 600_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        for (Ability value : PotPvPSI.getInstance().getAbilityHandler().getAbilities().values()) {
            if (value.getName().equalsIgnoreCase("TimeStone")) {
                continue;
            }

            if (value.hasCooldown(player)) {
                value.removeCooldown(player);
            }
        }

        this.applyCooldown(player);
    }
}