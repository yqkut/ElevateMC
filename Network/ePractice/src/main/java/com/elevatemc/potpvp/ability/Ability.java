package com.elevatemc.potpvp.ability;

import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Ability implements Listener {

    public Ability() {
        final List<String> finalLore = new ArrayList<>(this.getLore());

        this.hassanStack = ItemBuilder.of(this.getMaterial())
                .name(this.getDisplayName() == null ? this.getName() : this.getDisplayName())
                .setLore(this.getLore() == null ? new ArrayList<>() : finalLore)
                .build();

        PotPvPSI.getInstance().getServer().getPluginManager().registerEvents(this, PotPvPSI.getInstance());
    }

    public abstract String getName();
    public abstract Material getMaterial();
    public abstract String getDisplayName();
    public abstract List<String> getLore();
    public abstract long getCooldown();

    public ItemStack hassanStack;

    public boolean isSimilar(ItemStack itemStack) {

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        return itemStack.getType() == this.getMaterial() && itemStack.getItemMeta().getDisplayName().startsWith(this.hassanStack.getItemMeta().getDisplayName()) && itemStack.getItemMeta().getLore().get(0).equals(this.getLore().get(0));
    }

    public void removeCooldown(Player player) {
        PotPvPSI.getInstance().getAbilityHandler().getCooldown().remove(player.getUniqueId(), this);
    }

    public void applyCooldown(Player player) {
        long cooldown = this.getCooldown();

        sendCooldownMessage(player);
        PotPvPSI.getInstance().getAbilityHandler().applyCooldown(this, player);
    }

    public void applyCooldown(Player player, long time) {
        PotPvPSI.getInstance().getAbilityHandler().applyCooldown(this, player, time);
    }

    public void applyCooldown(Player player, boolean sendMessage) {
        if (sendMessage) {
            sendCooldownMessage(player);
        }
        PotPvPSI.getInstance().getAbilityHandler().applyCooldown(this, player);
    }

    private void sendCooldownMessage(Player player) {
        String name = this.getDisplayName();
        String time = TimeUtils.formatIntoDetailedString((int) (this.getCooldown() / 1000));
        player.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD +  "✔ " + ChatColor.GREEN + "You have used " + name + ChatColor.GREEN + " successfully and are now on cooldown for " + time + ".");
    }

    public boolean hasCooldown(Player player) {
        return this.hasCooldown(player, true);
    }

    public boolean hasCooldown(Player player, boolean sendMessage) {
        return this.hasCooldown(player, true, true);
    }

    public boolean hasCooldown(Player player, boolean sendMessage, boolean checkGlobal) {

        final long current = PotPvPSI.getInstance().getAbilityHandler().getRemaining(this, player);

        if (current > 0) {

            if (sendMessage) {
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + "You cannot use the " + this.getDisplayName() + ChatColor.RED + " for another " + ChatColor.BOLD.toString() + TimeUtils.formatIntoDetailedString((int) (current / 1000)) + ChatColor.RED + ".");
            }

            return true;
        }

        return false;
    }

    public long getRemaining(Player player) {
        return PotPvPSI.getInstance().getAbilityHandler().getRemaining(this, player);
    }

}