package com.elevatemc.potpvp.match;

import com.elevatemc.potpvp.setting.Setting;
import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.lobby.LobbyItems;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public final class MatchUtils {

    public static void resetInventory(Player player) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match match = matchHandler.getMatchSpectating(player);

        // because we lookup their match with getMatchSpectating this will also
        // return for players fighting in matches
        if (match == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv.getType().equals(InventoryType.CRAFTING)) {
            inv.clear();
        }
        player.setItemOnCursor(null);
        // don't give players who die (and cause the match to end)
        // a fire item, they'll be sent back to the lobby in a few seconds anyway
        if (match.getState() != MatchState.ENDING) {
            // if they've been on any team or are staff they'll be able to
            // use this item on at least 1 player. if they can't use it all
            // we just don't give it to them (UX purposes)
            boolean canViewInventories = player.hasPermission("potpvp.inventory.all");

            if (!canViewInventories) {
                for (MatchTeam team : match.getTeams()) {
                    if (team.getAllMembers().contains(player.getUniqueId())) {
                        canViewInventories = true;
                        break;
                    }
                }
            }

            // fill inventory with spectator items
            if (canViewInventories) {
                inventory.setItem(2, SpectatorItems.VIEW_INVENTORY_ITEM);
            }

            if (settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS)) {
                inventory.setItem(4, SpectatorItems.HIDE_SPECTATORS_ITEM);
            } else {
                inventory.setItem(4, SpectatorItems.SHOW_SPECTATORS_ITEM);
            }


            // this bit is correct; see SpectatorItems file for more
            if (partyHandler.hasParty(player)) {
                inventory.setItem(8, SpectatorItems.LEAVE_PARTY_ITEM);
            } else {
                inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);

                if (!followHandler.getFollowing(player).isPresent()) {
                    inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
                    inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

}