package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.KitItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class MatchAntidoteListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPotionDrinkEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().isSimilar(KitItems.ANTIDOTE_ITEM)) {
            event.setCancelled(true);
            player.setItemInHand(new ItemStack(Material.AIR));
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.POISON);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.removePotionEffect(PotionEffectType.WITHER);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.removePotionEffect(PotionEffectType.HUNGER);
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }
    }
}
