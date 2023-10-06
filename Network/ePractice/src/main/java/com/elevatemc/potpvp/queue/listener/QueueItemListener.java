package com.elevatemc.potpvp.queue.listener;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.menu.queue.QueueGameModeMenu;
import com.elevatemc.potpvp.listener.RankedMatchQualificationListener;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.queue.QueueItems;
import com.elevatemc.potpvp.util.ItemListener;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// This class followes a different organizational style from other item listeners
// because we need seperate listeners for ranked/unranked, we have methods which
// we call which generate a Consumer<Player> designed for either ranked/unranked,
// based on the argument passed. Returning Consumers makes this code slightly
// harder to follow, but saves us from a lot of duplication
public final class QueueItemListener extends ItemListener {
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, joinPartyConsumer(false));
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, joinPartyConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        Consumer<Player> leaveQueuePartyConsumer = player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // don't message, players who aren't leader shouldn't even get this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked) {
                if (rebootSoon()) {
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't join ranked queues with a reboot scheduled soon.");
                    return;
                }

                if (!RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                    int needed = RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't join ranked queues with less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " casual 1v1 wins. You need " + needed + " more wins!");
                    return;
                }
            }

            if (PotPvPValidation.canJoinQueue(player)) {
                new QueueGameModeMenu(gameMode -> {
                    queueHandler.joinQueue(player, gameMode, ranked);
                    player.closeInventory();
                }, ranked).openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean competitive) {
        return player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            if (competitive) {
                if (rebootSoon()) {
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't join competitive queues with a reboot scheduled soon.");
                    return;
                }

                for (UUID member : party.getMembers()) {
                    if (!RankedMatchQualificationListener.isQualified(member)) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(member);
                        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your party can't join competitive queues because " + UUIDUtils.name(member) + " has less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " casual 1v1 wins. They need " + needed + " more wins!");
                        return;
                    }
                }
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(party)) {
                new QueueGameModeMenu(gameMode -> {
                    queueHandler.joinQueue(party, gameMode, competitive);
                    player.closeInventory();
                }, competitive).openMenu(player);
            }
        };
    }

    private boolean rebootSoon() {
        return eLib.getInstance().getAutoRebootHandler().isRebooting() && eLib.getInstance().getAutoRebootHandler().getRebootSecondsRemaining() <= TimeUnit.MINUTES.toSeconds(5);
    }

}