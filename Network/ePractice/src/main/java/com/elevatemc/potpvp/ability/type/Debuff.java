package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Debuff extends Ability {

    public Debuff() {
        this.hassanStack = ItemBuilder.of(Material.RAW_FISH)
                .name(this.getDisplayName())
                .data((byte) 3)
                .setLore(this.getLore()).build();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.RAW_FISH;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Debuff Fish";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit a player 3 times to double");
        toReturn.add(Color.translate("&7debuff them for 20 seconds!"));

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

        int value = target.hasMetadata("DEBUFF_FISH") ? (target.getMetadata("DEBUFF_FISH").get(0).asInt()+1) : 1;

        target.setMetadata("DEBUFF_FISH", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(Color.translate("&6You have to hit &f" + target.getName() + " &6" + (3 - value) + " more time"  + (3-value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("DEBUFF_FISH", PotPvPSI.getInstance());

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount()-1);
        }

        damager.sendMessage("");
        damager.sendMessage(Color.translate("&cYou have hit &f" + target.getName() + " &cwith the " + this.getDisplayName() + "&c."));
        damager.sendMessage(Color.translate("&7They have been double debuffed."));
        damager.sendMessage("");

        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + " and have been double debuffed.");

        int duration = 20;
        Arrays.asList(
                new PotionEffect(PotionEffectType.POISON, duration * 20, 0),
                new PotionEffect(PotionEffectType.SLOW, duration * 20, 0)
        ).forEach(target::addPotionEffect);

        this.applyCooldown(damager);

    }

}