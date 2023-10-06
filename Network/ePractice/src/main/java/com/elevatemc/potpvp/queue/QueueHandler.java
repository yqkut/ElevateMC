package com.elevatemc.potpvp.queue;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.queue.listener.QueueGeneralListener;
import com.elevatemc.potpvp.queue.listener.QueueItemListener;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class QueueHandler {

    public static final int RANKED_WINDOW_GROWTH_PER_SECOND = 5;

    private static final String JOIN_SOLO_MESSAGE = ChatColor.DARK_AQUA + "⚔ " + ChatColor.AQUA + "You joined the " + ChatColor.DARK_AQUA + "%s %s" + ChatColor.AQUA + " queue" + ".";
    private static final String LEAVE_SOLO_MESSAGE = ChatColor.DARK_AQUA + "⚠ " + ChatColor.AQUA + "You left the " + ChatColor.DARK_AQUA + "%s %s" + ChatColor.AQUA + " queue" + ".";
    private static final String WAIT_SOLO_MESSAGE = ChatColor.DARK_GRAY + " ⌛" + ChatColor.GRAY + " Please wait while we find an opponent…";
    private static final String JOIN_PARTY_MESSAGE = ChatColor.DARK_AQUA + "⚔ " + ChatColor.AQUA + "Your party has queued for " + ChatColor.DARK_AQUA + "%s %s" + ChatColor.AQUA + ".";
    private static final String LEAVE_PARTY_MESSAGE = ChatColor.DARK_AQUA + "⚠ " + ChatColor.AQUA + "Your party is no longer queued for " + ChatColor.DARK_AQUA + "%s %s" + ChatColor.AQUA + ".";

    private static final String WAIT_PARTY_MESSAGE = ChatColor.DARK_GRAY + " ⌛" + ChatColor.GRAY + " Please wait while we find an opponents…";


    // we never call .put outside of the constructor so no concurrency is needed
    // (GameMode type, boolean ranked) -> MatchQueue
    private final Table<GameMode, Boolean, MatchQueue> soloQueues = HashBasedTable.create();
    private final Table<GameMode, Boolean, MatchQueue> partyQueues = HashBasedTable.create();

    // maps players (and parties) to their entry for fast O(1) lookup
    private final Map<UUID, SoloMatchQueueEntry> soloQueueCache = new ConcurrentHashMap<>();
    private final Map<Party, PartyMatchQueueEntry> partyQueueCache = new ConcurrentHashMap<>();

    // because this is called very often (it's on the lobby scoreboard)
    // we cache every second (per GameMode counts aren't cached, however)
    @Getter private int queuedCount = 0;

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueGeneralListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), PotPvPSI.getInstance());

        Bukkit.getScheduler().runTaskTimer(PotPvPSI.getInstance(), () -> {
            soloQueues.values().forEach(MatchQueue::tick);
            partyQueues.values().forEach(MatchQueue::tick);

            int i = 0;

            for (MatchQueue queue : soloQueues.values()) {
                i += queue.countPlayersQueued();
            }

            for (MatchQueue queue : partyQueues.values()) {
                i += queue.countPlayersQueued();
            }

            queuedCount = i;
        }, 20L, 20L);
    }

    public void addQueues(GameMode gameMode) {
        soloQueues.put(gameMode, true, new MatchQueue(gameMode, true));
        soloQueues.put(gameMode, false, new MatchQueue(gameMode, false));

        partyQueues.put(gameMode, true, new MatchQueue(gameMode, true));
        partyQueues.put(gameMode, false, new MatchQueue(gameMode, false));
    }

    public void removeQueues(GameMode gameMode) {
        soloQueues.remove(gameMode, true);
        soloQueues.remove(gameMode, false);

        partyQueues.remove(gameMode, true);
        partyQueues.remove(gameMode, false);
    }

    public int countPlayersQueued(GameMode gameMode, boolean ranked) {
        return soloQueues.get(gameMode, ranked).countPlayersQueued() +
               partyQueues.get(gameMode, ranked).countPlayersQueued();
    }

    public boolean joinQueue(Player player, GameMode gameMode, boolean competitive) {
        if (!PotPvPValidation.canJoinQueue(player)) {
            return false;
        }

        MatchQueue queue = soloQueues.get(gameMode, competitive);
        SoloMatchQueueEntry entry = new SoloMatchQueueEntry(queue, player.getUniqueId());

        queue.addToQueue(entry);
        soloQueueCache.put(player.getUniqueId(), entry);

        player.sendMessage(String.format(JOIN_SOLO_MESSAGE, competitive ? "Competitive" : "Casual", gameMode.getName()));
        player.sendMessage(String.format(WAIT_SOLO_MESSAGE));
        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean leaveQueue(Player player, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(player.getUniqueId());

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();

        queue.removeFromQueue(entry);
        soloQueueCache.remove(player.getUniqueId());

        if (!silent) {
            player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, queue.isCompetitive() ? "Competitive" : "Casual", queue.getGameMode().getName()));
        }

        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean joinQueue(Party party, GameMode gameMode, boolean ranked) {
        if (!PotPvPValidation.canJoinQueue(party)) {
            return false;
        }

        MatchQueue queue = partyQueues.get(gameMode, ranked);
        PartyMatchQueueEntry entry = new PartyMatchQueueEntry(queue, party);

        queue.addToQueue(entry);
        partyQueueCache.put(party, entry);

        party.message(String.format(JOIN_PARTY_MESSAGE, ranked ? "Competitive" : "Casual", gameMode.getName()));
        party.resetInventoriesDelayed();
        return true;
    }

    public boolean leaveQueue(Party party, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(party);

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();

        queue.removeFromQueue(entry);
        partyQueueCache.remove(party);

        if (!silent) {
            party.message(String.format(LEAVE_PARTY_MESSAGE, queue.isCompetitive() ? "Competitive" : "Casual", queue.getGameMode().getName()));
        }

        party.resetInventoriesDelayed();
        return true;
    }

    public boolean isQueued(UUID player) {
        return soloQueueCache.containsKey(player);
    }

    public boolean isQueuedRanked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && entry.getQueue().isCompetitive();
    }

    public boolean isQueuedUnranked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && !entry.getQueue().isCompetitive();
    }

    public SoloMatchQueueEntry getQueueEntry(UUID player) {
        return soloQueueCache.get(player);
    }

    public boolean isQueued(Party party) {
        return partyQueueCache.containsKey(party);
    }

    public boolean isQueuedRanked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && entry.getQueue().isCompetitive();
    }

    public boolean isQueuedUnranked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && !entry.getQueue().isCompetitive();
    }


    public PartyMatchQueueEntry getQueueEntry(Party party) {
        return partyQueueCache.get(party);
    }

    void removeFromQueueCache(MatchQueueEntry entry) {
        if (entry instanceof SoloMatchQueueEntry) {
            soloQueueCache.remove(((SoloMatchQueueEntry) entry).getPlayer());
        } else if (entry instanceof PartyMatchQueueEntry) {
            partyQueueCache.remove(((PartyMatchQueueEntry) entry).getParty());
        }
    }

}