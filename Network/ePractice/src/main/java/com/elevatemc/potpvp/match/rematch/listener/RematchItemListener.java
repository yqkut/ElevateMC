package com.elevatemc.potpvp.match.rematch.listener;

import com.elevatemc.potpvp.duel.command.AcceptCommand;
import com.elevatemc.potpvp.duel.command.DuelCommand;
import com.elevatemc.potpvp.match.rematch.RematchHandler;
import com.elevatemc.potpvp.match.rematch.RematchItems;
import com.elevatemc.potpvp.match.rematch.RematchData;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.ItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class RematchItemListener extends ItemListener {

    public RematchItemListener(RematchHandler rematchHandler) {
        addHandler(RematchItems.REQUEST_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                DuelCommand.duel(player, target, rematchData.getGameMode(), rematchData.getArenaName());

                InventoryUtils.resetInventoryDelayed(player);
                InventoryUtils.resetInventoryDelayed(target);
            }
        });

        addHandler(RematchItems.SENT_REMATCH_ITEM, p -> p.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have already sent a rematch request."));

        addHandler(RematchItems.ACCEPT_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                AcceptCommand.accept(player, target);
            }
        });
    }

}