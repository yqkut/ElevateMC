package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Vampire extends Ability {
    public Vampire() {
        super();
        this.hassanStack.setDurability((byte) 65);
        ItemMeta meta = this.hassanStack.getItemMeta();

//        meta.addEnchant(glow, 1, true);
        this.hassanStack.setItemMeta(meta);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.MONSTER_EGG;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Vampire Effect";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Spawn 3 bats that give you the passive");
        toReturn.add(ChatColor.GRAY + "bard effects: strength, regeneration,");
        toReturn.add(ChatColor.GRAY + "and resistance. Each bat gives a");
        toReturn.add(ChatColor.GRAY + "different effect.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.updateInventory();

        final Bat bat1 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
        final Bat bat2 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
        final Bat bat3 = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);

        bat1.setAwake(true);
        bat1.setCustomNameVisible(true);
        bat1.setCustomName(ChatColor.DARK_RED + "✖ " + ChatColor.RED + player.getName() + "'s Vampire #1");

        bat2.setAwake(true);
        bat2.setCustomNameVisible(true);
        bat2.setCustomName(ChatColor.DARK_RED + "✖ " + ChatColor.RED + player.getName() + "'s Vampire #2");

        bat3.setAwake(true);
        bat3.setCustomNameVisible(true);
        bat3.setCustomName(ChatColor.DARK_RED + "✖ " + ChatColor.RED + player.getName() + "'s Vampire #3");

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {

            if (!bat1.isDead()) {
                bat1.remove();
            }

            if (!bat2.isDead()) {
                bat2.remove();
            }

            if (!bat3.isDead()) {
                bat3.remove();
            }

            if (player.isOnline()) {
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your bats have despawned!");
            }

        }, 20*60*3);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (bat1.isDead() && bat2.isDead() && bat3.isDead()) {
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "All your bats are now dead!");
                    this.cancel();
                    return;
                }

                if (!bat1.isDead() && bat1.getLocation().distance(player.getLocation()) <= 15) {
                    bat1.setAwake(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0), true);
                }

                if (!bat2.isDead() && bat2.getLocation().distance(player.getLocation()) <= 15) {
                    bat2.setAwake(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0), true);
                }

                if (!bat3.isDead() && bat3.getLocation().distance(player.getLocation()) <= 15) {
                    bat3.setAwake(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0), true);
                }
            }
        }.runTaskTimer(PotPvPSI.getInstance(), 0, 40);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Bat)) {
            return;
        }

        final Bat bat = (Bat) event.getEntity();
        final String customName = bat.getCustomName();

        final int id = Integer.parseInt(customName.charAt(customName.length() - 1) + "");

        final String playersName = ChatColor.stripColor(customName).replace("'s Vampire #" + id, "");

        final Player player = PotPvPSI.getInstance().getServer().getPlayer(playersName);

        if (player == null) {
            return;
        }

        switch (id) {
            case 1:
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your Resistance bat has died!");
                break;
            case 2:
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your Regeneration bat has died!");
                break;
            case 3:
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your Strength bat has died!");
                break;
        }
    }
}
