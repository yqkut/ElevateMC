package com.elevatemc.potpvp.util;

import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.lobby.LobbyUtils;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class InventoryUtils {

    public static final long RESET_DELAY_TICKS = 2L;

    public static void resetInventoryDelayed(Player player) {
        Runnable task = () -> resetInventoryNow(player);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), task, RESET_DELAY_TICKS);
    }

    public static void resetInventoryNow(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            MatchUtils.resetInventory(player);
        } else {
            LobbyUtils.resetInventory(player);
        }
    }

    public static void removeAmountFromInventory(Inventory inventory, ItemStack item, int amount) {
        for(ItemStack invItem : inventory.getContents()) {
            if(invItem != null) {
                if(invItem.isSimilar(item)) {
                    int preAmount = invItem.getAmount();
                    int newAmount = Math.max(0, preAmount - amount);
                    amount = Math.max(0, amount - preAmount);
                    invItem.setAmount(newAmount);
                    if(amount == 0) {
                        break;
                    }
                }
            }
        }
    }

    public static boolean isEmpty(Player player) {
        for(ItemStack content : player.getInventory().getContents()) {
            if(content != null) return false;
        }

        return true;
    }

}