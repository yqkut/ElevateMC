package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Invisibility extends Ability {
    public static PotionEffect EFFECT = new PotionEffect(PotionEffectType.INVISIBILITY,(3*60)*20,2,false);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.INK_SACK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Invisibility";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "When you right click this item");
        toReturn.add(ChatColor.GRAY + "your armor will no longer be visible.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack.getAmount() == 1) {
            event.getPlayer().setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        event.getPlayer().addPotionEffect(EFFECT, true);

        this.sendRestorePacket(event.getPlayer(), PotPvPSI.getInstance().getServer().getOnlinePlayers(),true);

        this.applyCooldown(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || (!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
            return;
        }

        final Player player = (Player) event.getEntity();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player, PotPvPSI.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            ((Player)event.getEntity()).sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " You have been hit and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamagerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || (!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
            return;
        }

        final Player player = (Player) event.getDamager();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player, PotPvPSI.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " You have hit a player and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPotionEffectExpire(PotionEffectExpireEvent event) {
        this.onPotionEffectRemove(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPotionEffectRemove(PotionEffectRemoveEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!event.getEffect().getType().equals(EFFECT.getType()) || event.getEffect().getAmplifier() != EFFECT.getAmplifier()) {
            return;
        }

        final Player player = (Player) event.getEntity();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player, PotPvPSI.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            event.getEntity().sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " Your " + ChatColor.WHITE + "Invisibility" + ChatColor.YELLOW + " has expired and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    private void sendRestorePacket(Player player, Collection<? extends Player> players, boolean clear) {

        final List<PacketPlayOutEntityEquipment> packets = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int id = player.getEntityId();
            int slot = i+1;
            net.minecraft.server.v1_8_R3.ItemStack item = clear ? CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)) : CraftItemStack.asNMSCopy(player.getInventory().getArmorContents()[i]);


            final PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(id, slot, item);

            packets.add(packet);
        }

        players.stream().filter(it -> it.getUniqueId() != player.getUniqueId()).forEach(it -> packets.forEach(packet -> {

            try {
                PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
                playerConnection.sendPacket(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }));

    }
}
