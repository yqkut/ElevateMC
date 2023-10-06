package com.elevatemc.potpvp.lobby;

import com.elevatemc.potpvp.events.EventItems;
import com.elevatemc.potpvp.hctranked.game.*;
import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.duel.DuelHandler;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.kit.KitItems;
import com.elevatemc.potpvp.kit.menu.editkit.EditKitMenu;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.party.PartyItems;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.queue.QueueItems;
import com.elevatemc.potpvp.match.rematch.RematchData;
import com.elevatemc.potpvp.match.rematch.RematchHandler;
import com.elevatemc.potpvp.match.rematch.RematchItems;
import com.elevatemc.elib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {
        // prevents players with the kit editor from having their
        // inventory updated (kit items go into their inventory)
        // also, admins in GM don't get invs updated (to prevent annoying those editing kits)
        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        RankedGameHandler gameHandler = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv.getType().equals(InventoryType.CRAFTING)) {
            inv.clear();
        }
        player.setItemOnCursor(null);

        RankedGame game = gameHandler.getJoinedGame(player);
        if (game != null) {
            renderRankedGameItems(player, inventory, game);
        }  else if (partyHandler.hasParty(player)) {
            renderPartyItems(player, inventory, partyHandler.getParty(player));
        } else {
            renderSoloItems(player, inventory);
        }

        if(player.hasPermission("potpvp.lobby.flight")) {
            player.setAllowFlight(true);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    private void renderRankedGameItems(Player player, PlayerInventory inventory, RankedGame game) {
        RankedGameTeam team = game.getTeam(player);

        inventory.setItem(0, RankedGameItems.GAME_INFO);
        inventory.setItem(1, RankedGameItems.ASSIGN_CLASSES);
        if (team.getCaptain().equals(player.getUniqueId())) {
            if (team.isReady()) {
                inventory.setItem(2, RankedGameItems.READY);
            } else {
                inventory.setItem(2, RankedGameItems.NOT_READY);
            }

        }
        inventory.setItem(8, RankedGameItems.LEAVE_GAME);
    }

    private void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        if (party.isLeader(player.getUniqueId())) {
            int partySize = party.getMembers().size();

            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(1, QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(1, QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(2, QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(2, QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM);
                }
            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(1, PartyItems.START_TEAM_SPLIT_ITEM);
                inventory.setItem(2, PartyItems.START_FFA_ITEM);
                inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
            }

        } else {
            int partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
            }
        }

        inventory.setItem(0, PartyItems.PARTY_INFO);
        inventory.setItem(6, PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(7, KitItems.OPEN_EDITOR_ITEM);
        inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
    }

    private void renderSoloItems(Player player, PlayerInventory inventory) {
        RematchHandler rematchHandler = PotPvPSI.getInstance().getRematchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();

        boolean specMode = lobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();

        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);

        if (specMode || followingSomeone) {
            if (!followingSomeone) {
                inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
                inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            } else {
                inventory.setItem(8, LobbyItems.UNFOLLOW_ITEM);
            }
        } else {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (target != null) {
                    if (duelHandler.findInvite(player, target) != null) {
                        // if we've sent an invite to them
                        inventory.setItem(2, RematchItems.SENT_REMATCH_ITEM);
                    } else if (duelHandler.findInvite(target, player) != null) {
                        // if they've sent us an invite
                        inventory.setItem(2, RematchItems.ACCEPT_REMATCH_ITEM);
                    } else {
                        // if no one has sent an invite
                        inventory.setItem(2, RematchItems.REQUEST_REMATCH_ITEM);
                    }
                }
            }

            if (queueHandler.isQueuedRanked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else if (queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else {
                inventory.setItem(0, QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(1, QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                inventory.setItem(4, LobbyItems.CREATE_TEAM);
                inventory.setItem(7, LobbyItems.PLAYER_STATISTICS);
                inventory.setItem(8, KitItems.OPEN_EDITOR_ITEM);

                final ItemStack eventItem = EventItems.getEventItem();
                if(eventItem != null) inventory.setItem(3, EventItems.getEventItem());
            }
        }
    }
}