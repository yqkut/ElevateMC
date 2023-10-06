package com.elevatemc.potpvp.pvpclasses.pvpclasses;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.pvpclasses.PvPClass;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import com.elevatemc.potpvp.pvpclasses.pvpclasses.bard.BardEffect;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BardClass extends PvPClass implements Listener {

    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public final Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public final Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    @Getter private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter private static Map<String, Integer> energy = new ConcurrentHashMap<>();
    private static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);

    public static final int BARD_RANGE = 30;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final int MAX_ENERGY = 100;
    public static final int ENERGY_REGEN_PER_SECOND = 1;

    public BardClass() {
        super("Bard", 15, "GOLD_", null);

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45, ChatColor.RED + "Strength II"));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20, ChatColor.AQUA + "Speed III"));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25, ChatColor.WHITE + "Jump Boost VII"));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40, ChatColor.BLUE + "Resistance III"));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40, ChatColor.LIGHT_PURPLE + "Regeneration III"));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40, ChatColor.GOLD + "Fire Resistance I"));
        BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergyAndName(25, ChatColor.AQUA + "Food"));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergyAndName(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35, ChatColor.GRAY + "Wither II"));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));


        new BukkitRunnable() {

            public void run() {
                for (Player player : PotPvPSI.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, BardClass.this)) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Bard Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(PotPvPSI.getInstance(), 15L, 20L);
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!(this.qualifies(player.getInventory()))) {
            super.tick(player);
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType())) {
            // CUSTOM
            if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE && getLastEffectUsage().containsKey(player.getName()) && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                return;
            }

            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }
        super.tick(player);
    }


    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);
        int appliedTo = giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        if (negative) {
            appliedTo--;
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "☘ " + ChatColor.AQUA + "You have given " + bardEffect.getName() + ChatColor.AQUA + " to " + ChatColor.DARK_AQUA + appliedTo + ChatColor.AQUA + " enemies.");
        } else {
            if (appliedTo > 1) {
                if (bardEffect.getPotionEffect() != null && !bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    // Remove the bard from the amount of people the effect has been given to. Ignores strength since the bard wouldnt have gotten added to it anyways.
                    appliedTo--;
                }
                if (appliedTo > 1) {
                    event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "☘ " + ChatColor.AQUA + "You have given " + bardEffect.getName() + ChatColor.AQUA + " to " + ChatColor.DARK_AQUA + appliedTo + ChatColor.AQUA + " teammates.");
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "☘ " + ChatColor.AQUA + "You have given " + bardEffect.getName() + ChatColor.AQUA + " to " + ChatColor.DARK_AQUA + appliedTo + ChatColor.AQUA + " teammate.");
                }

            } else {
                if (appliedTo == 1) {
                    event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "☘ " + ChatColor.AQUA + "You have given " + bardEffect.getName() + ChatColor.AQUA + " to yourself.");
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "☘ " + ChatColor.AQUA + "You have given " + bardEffect.getName() + ChatColor.AQUA + " to no one.");
                }

            }
        }

        if (event.getPlayer().getItemInHand().getAmount() == 1) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            event.getPlayer().updateInventory();
        } else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    public int giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        int appliedTo = 0;
        for (Player player : getNearbyPlayers(source, friendly)) {
            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null && bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }

            appliedTo++;
            if (bardEffect.getPotionEffect() != null) {
                smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
        return appliedTo;
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case WHEAT:
                for (Player nearbyPlayer : getNearbyPlayers(player, true)) {
                    nearbyPlayer.setFoodLevel(20);
                    nearbyPlayer.setSaturation(10F);
                }

                break;
            case FERMENTED_SPIDER_EYE:


                break;
            default:
                PotPvPSI.getInstance().getLogger().warning("No custom Bard effect defined for " + material + ".");
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
        MatchTeam sourceTeam = match.getTeam(player.getUniqueId());

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE / 2, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.getAliveMembers().contains(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

}