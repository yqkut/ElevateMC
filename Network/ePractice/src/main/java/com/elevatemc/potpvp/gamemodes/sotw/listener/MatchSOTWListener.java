package com.elevatemc.potpvp.gamemodes.sotw.listener;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchCountdownStartEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class MatchSOTWListener implements Listener {

    public ItemStack[] helmets = {
        ItemBuilder.of(Material.DIAMOND_HELMET).build(),
        ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
        ItemBuilder.of(Material.IRON_HELMET).build(),
        ItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build()
    };
    public ItemStack[] chestplates = {
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
        ItemBuilder.of(Material.IRON_CHESTPLATE).build(),
        ItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
    };
    public ItemStack[] leggings = {
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).build(),
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
        ItemBuilder.of(Material.IRON_LEGGINGS).build(),
        ItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build()
    };
    public ItemStack[] boots = {
        ItemBuilder.of(Material.DIAMOND_BOOTS).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
        ItemBuilder.of(Material.IRON_BOOTS).build(),
        ItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
        ItemBuilder.of(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build()
    };
    public ItemStack[] swords = {
        ItemBuilder.of(Material.DIAMOND_SWORD).build(),
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build(),
        ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).build(),
        ItemBuilder.of(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build(),
        ItemBuilder.of(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).build()
    };
    public ItemStack[] pearls = {
        ItemBuilder.of(Material.ENDER_PEARL).amount(1).build(),
        ItemBuilder.of(Material.ENDER_PEARL).amount(2).build(),
        ItemBuilder.of(Material.ENDER_PEARL).amount(3).build(),
    };
    public ItemStack[] heals = {
        ItemBuilder.of(Material.POTION).data((short)16389).build(), // Splash I
        ItemBuilder.of(Material.POTION).data((short)16421).build(), // Splash II
        ItemBuilder.of(Material.POTION).data((short)8197).build(), // Drink I
        ItemBuilder.of(Material.POTION).data((short)8229).build(), // Drink II
    };

    public ItemStack[] foods = {
        ItemBuilder.of(Material.COOKED_BEEF).amount(8).build(),
        ItemBuilder.of(Material.BREAD).amount(12).build(),
        ItemBuilder.of(Material.CARROT_ITEM).amount(6).build()
    };

    public ItemStack[] extras = {
        ItemBuilder.of(Material.FISHING_ROD).build(),
        ItemBuilder.of(Material.EGG).amount(8).build(),
        ItemBuilder.of(Material.SNOW_BALL).amount(16).build()
    };

    @EventHandler
    public void matchCountDownStartEvent(MatchCountdownStartEvent e) {
        if (!e.getMatch().getGameMode().getId().equals("SOTW")) return;

        for (MatchTeam team : e.getMatch().getTeams()) {
            team.forEachAlive(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                setupInventory(p);
            });
        }
    }

    public void setupInventory(Player player) {
        ItemStack randomHelmet = helmets[PotPvPSI.RANDOM.nextInt(helmets.length)];
        ItemStack randomChestplate = chestplates[PotPvPSI.RANDOM.nextInt(chestplates.length)];
        ItemStack randomLeggings = leggings[PotPvPSI.RANDOM.nextInt(leggings.length)];
        ItemStack randomBoots = boots[PotPvPSI.RANDOM.nextInt(boots.length)];
        ItemStack randomSword = swords[PotPvPSI.RANDOM.nextInt(swords.length)];
        ItemStack randomPearls = pearls[PotPvPSI.RANDOM.nextInt(pearls.length)];
        ItemStack randomExtra = extras[PotPvPSI.RANDOM.nextInt(extras.length)];
        ItemStack randomFood = foods[PotPvPSI.RANDOM.nextInt(foods.length)];

        List<ItemStack> randomHeals = new ArrayList<>();
        for (int i = PotPvPSI.RANDOM.nextInt(5); i > 0; i--) {
            randomHeals.add(heals[PotPvPSI.RANDOM.nextInt(heals.length)]);
        }
        PlayerInventory i = player.getInventory();
        i.setHelmet(randomHelmet);
        i.setChestplate(randomChestplate);
        i.setLeggings(randomLeggings);
        i.setBoots(randomBoots);
        i.addItem(randomSword, randomPearls, randomExtra);
        for (ItemStack randomHeal : randomHeals) {
            i.addItem(randomHeal);
        }
        i.setItem(8, randomFood);
    }
}