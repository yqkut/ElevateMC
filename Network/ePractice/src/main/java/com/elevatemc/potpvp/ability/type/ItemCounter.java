package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.util.Color;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemCounter extends Ability {
    @Getter
    public static List<UUID> cache = new ArrayList<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.COMPASS;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Item Counter";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit an enemy to discover how many");
        toReturn.add(ChatColor.GRAY + "health potions and ability items they have!");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 35_000L;
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

        final Map<Ability, Integer> abilities = new HashMap<>();

        int crapples = 0;
        int godApples = 0;

        for (ItemStack content : target.getInventory().getContents()) {

            if (content == null || content.getType() == Material.AIR) {
                continue;
            }

            if (content.getType() == Material.GOLDEN_APPLE && content.getData().getData() == 0) {
                crapples += content.getAmount();
                continue;
            }

            if (content.getType() == Material.GOLDEN_APPLE && content.getData().getData() == 1) {
                godApples += content.getAmount();
                continue;
            }

            final Ability ability = PotPvPSI.getInstance().getAbilityHandler().getAbilities().values().stream().filter(it -> it.isSimilar(content)).findFirst().orElse(null);

            if (ability == null) {
                continue;
            }

            int amount = abilities.getOrDefault(ability, 0)+content.getAmount();

            abilities.put(ability, amount);
        }

        int amount = (int) Arrays.stream(target.getInventory().getContents()).filter(it -> it != null && it.getType() == Material.POTION && it.getDurability() == 16421).count();

        damager.sendMessage("");
        damager.sendMessage(Color.translate(target.getName() + " &chas &f" + amount + " &chealth potions in their inventory."));
        damager.sendMessage(Color.translate(target.getName() + " &chas &f" + crapples + " &ccrapples in their inventory."));
        damager.sendMessage(Color.translate(target.getName() + " &chas &f" + godApples + " &cgod apples in their inventory."));
        if (!abilities.isEmpty()) {
            damager.sendMessage(Color.translate("&6&lAbilities:"));
        }

        for (Map.Entry<Ability, Integer> abilityIntegerEntry : abilities.entrySet()) {
            damager.sendMessage(Color.translate("&6- " + abilityIntegerEntry.getKey().getDisplayName() + "&7: &f" + abilityIntegerEntry.getValue()));
        }

        damager.sendMessage("");

        final ItemStack itemStack = damager.getItemInHand();

        if (itemStack.getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        this.applyCooldown(damager);
    }
}