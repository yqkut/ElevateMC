package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SwitchStick extends Ability {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.STICK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Switch Stick";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit a player to turn their head 180 degrees.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(1L) + TimeUnit.SECONDS.toMillis(30);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (damager.getItemInHand() == null || !this.isSimilar(damager.getItemInHand())) {
            return;
        }

        if (PvPClassHandler.getPvPClass(target) != null) {
            damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use a " + this.getDisplayName() + ChatColor.RED + " on a " + ChatColor.WHITE + Objects.requireNonNull(PvPClassHandler.getPvPClass(target)).getName() + ChatColor.RED + ".");
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final Location location = target.getLocation();
        location.setYaw(location.getYaw()+180.0F);
        target.teleport(location);

        target.sendMessage(damager.getName() + ChatColor.RED + " has used the " + this.getDisplayName() + ChatColor.RED + " on you!");

        final ItemStack itemStack = damager.getItemInHand();

        if (itemStack.getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        this.applyCooldown(damager);
    }
}