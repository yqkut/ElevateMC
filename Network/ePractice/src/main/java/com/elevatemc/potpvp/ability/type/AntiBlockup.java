package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.PotionUtil;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Material.*;

public class AntiBlockup extends Ability {
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(FENCE_GATE, FURNACE, BURNING_FURNACE, BREWING_STAND, CHEST, HOPPER, DISPENSER, WOODEN_DOOR, STONE_BUTTON, WOOD_BUTTON, TRAPPED_CHEST, TRAP_DOOR, LEVER, DROPPER, ENCHANTMENT_TABLE, BED_BLOCK, ANVIL, BEACON);

    @Getter
    public static Map<UUID, Long> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Anti-Blockup";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "When you hit a player with this");
        toReturn.add(ChatColor.GRAY + "3 times they may not block up for 15 seconds.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
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

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        int value = target.hasMetadata("ANTI_BUILD") ? (target.getMetadata("ANTI_BUILD").get(0).asInt() + 1) : 1;

        if (cache.containsKey(target.getUniqueId())) {
            damager.sendMessage(Color.translate("&c" + target.getName() + " already can't place blocks for &l" + TimeUtils.formatIntoMMSS((int) (cache.get(target.getUniqueId()) - System.currentTimeMillis()) / 1000) + "&c."));
            return;
        }

        target.setMetadata("ANTI_BUILD", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(Color.translate("&6You have to hit &f" + target.getName() + " &6" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("ANTI_BUILD", PotPvPSI.getInstance());

        cache.put(target.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        damager.sendMessage("");
        damager.sendMessage(Color.translate("&6You have hit &f" + target.getName() + " &6with the " + this.getDisplayName() + "&6."));
        damager.sendMessage(Color.translate("&6They may not place blocks for &f15 seconds&6."));
        damager.sendMessage("");

        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + " and may not place blocks for 15 seconds.");

        this.applyCooldown(damager);

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            AntiBlockup.cache.remove(target.getUniqueId());

            target.sendMessage("");
            target.sendMessage(Color.translate("&cThe &f" + this.getDisplayName() + " &chas expired! You may now place blocks!"));
            target.sendMessage("");
        }, 20*15);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not block up as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not break blocks as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlate(PlayerInteractEvent event) {
        if ((event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null || !event.getClickedBlock().getType().name().contains("PLATE"))) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();

        if (!NO_INTERACT.contains(clickedBlock.getType()) && !clickedBlock.getType().name().contains("SIGN")) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not interact with " + event.getClickedBlock().getType().name().toLowerCase().replace("_", " ") + "s as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");
    }


}