package com.elevatemc.potpvp.statistics;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import com.elevatemc.potpvp.util.MongoUtils;
import com.elevatemc.elib.util.UUIDUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class StatisticsHandler implements Listener {
    
    private static MongoCollection<Document> COLLECTION;
    private Map<UUID, Map<String, Map<Statistic, Double>>> statisticsMap;
    
    public StatisticsHandler() {
        COLLECTION = MongoUtils.getCollection("playerStatistics");
        statisticsMap = Maps.newConcurrentMap();
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPSI.getInstance(), () -> {
            
            long start = System.currentTimeMillis();
            statisticsMap.keySet().forEach(this::saveStatistics);
            Bukkit.getLogger().info("Saved " + statisticsMap.size() + " statistics in " + (System.currentTimeMillis() - start) + "ms.");
            
        }, 30 * 20, 30 * 20);
    }
    
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            loadStatistics(event.getPlayer().getUniqueId());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            saveStatistics(event.getPlayer().getUniqueId());
            unloadStatistics(event.getPlayer().getUniqueId());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMatchEnd(MatchTerminateEvent event) {
        Match match = event.getMatch();
        GameMode gameMode = match.getGameMode();

        if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) return;
        
        match.getWinningPlayers().forEach(uuid -> {
            incrementStat(uuid, Statistic.WINS, match.getGameMode());
        });
        
        match.getLosingPlayers().forEach(uuid -> {
            incrementStat(uuid, Statistic.LOSSES, match.getGameMode());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = died.getKiller();
        
        Match diedMatch = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(died);
        
        if (diedMatch == null) {
            return;
        }

        GameMode gameMode = diedMatch.getGameMode();
        if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
            return;
        }
        
        incrementStat(died.getUniqueId(), Statistic.DEATHS, diedMatch.getGameMode());
        
        if (killer != null) {
            incrementStat(killer.getUniqueId(), Statistic.KILLS, diedMatch.getGameMode());
        }
    }
    
    public void loadStatistics(UUID uuid) {
        Document document = COLLECTION.find(new Document("_id", uuid.toString())).first();

        if (document == null) {
            document = new Document();
        }
        
        document.put("lastUsername", UUIDUtils.name(uuid));

        final Document finalDocument = document;
        Map<String, Map<Statistic, Double>> subStatisticsMap = Maps.newHashMap();

        GameMode.getAll().forEach(gameMode -> {
            Document subStatisticsDocument = finalDocument.containsKey(gameMode.getId()) ? finalDocument.get(gameMode.getId(), Document.class) : new Document();

            Map<Statistic, Double> statsMap = Maps.newHashMap();
            for (Statistic statistic : Statistic.values()) {
                Double value = Objects.firstNonNull(subStatisticsDocument.get(statistic.name(), Double.class), 0D);
                statsMap.put(statistic, value);
            }

            subStatisticsMap.put(gameMode.getId(), statsMap);
        });

        if (finalDocument.containsKey("GLOBAL")) {
            Document subStatisticsDocument = finalDocument.containsKey("GLOBAL") ? finalDocument.get("GLOBAL", Document.class) : new Document();

            Map<Statistic, Double> statsMap = Maps.newHashMap();
            for (Statistic statistic : Statistic.values()) {
                Double value = Objects.firstNonNull(subStatisticsDocument.get(statistic.name(), Double.class), 0D);
                statsMap.put(statistic, value);
            }

            subStatisticsMap.put("GLOBAL", statsMap);
        } else {
            subStatisticsMap.put("GLOBAL", Maps.newHashMap());
        }

        statisticsMap.put(uuid, subStatisticsMap);
    }

    public void saveStatistics(UUID uuid) {
        Map<String, Map<Statistic, Double>> subMap = statisticsMap.get(uuid);
        if (subMap == null) {
            return;
        }

        Document toInsert = new Document();
        subMap.entrySet().forEach(entry -> {
            Document typeStats = new Document();
            entry.getValue().entrySet().forEach(subEntry -> {
                typeStats.put(subEntry.getKey().name(), subEntry.getValue());
            });

            toInsert.put(entry.getKey(), typeStats);
        });
        
        toInsert.put("lastUsername", UUIDUtils.name(uuid));

        COLLECTION.updateOne(new Document("_id", uuid.toString()), new Document("$set", toInsert), MongoUtils.UPSERT_OPTIONS);
    }

    public void unloadStatistics(UUID uuid) {
        statisticsMap.remove(uuid);
    }

    public void incrementStat(UUID uuid, Statistic statistic, GameMode gameMode) {
        boolean shouldUpdateWLR = statistic == Statistic.WINS || statistic == Statistic.LOSSES;
        boolean shouldUpdateKDR = statistic == Statistic.KILLS || statistic == Statistic.DEATHS;

        if (!statisticsMap.containsKey(uuid)) return; // not loaded, so prob offline so it won't save anyway

        incrementEntry(uuid, gameMode.getId(), statistic);
        incrementEntry(uuid, "GLOBAL", statistic);

        if (shouldUpdateWLR) {
            recalculateWLR(uuid, gameMode);
        } else if (shouldUpdateKDR) {
            recalculateKDR(uuid, gameMode);
        }
    }

    private void recalculateWLR(UUID uuid, GameMode gameMode) {
        double totalWins = getStat(uuid, Statistic.WINS, gameMode.getId());
        double totalLosses = getStat(uuid, Statistic.LOSSES, gameMode.getId());

        double ratio = totalWins / Math.max(totalLosses, 1);
        statisticsMap.get(uuid).get(gameMode.getId()).put(Statistic.WLR, ratio);

        totalWins = getStat(uuid, Statistic.WINS, "GLOBAL");
        totalLosses = getStat(uuid, Statistic.LOSSES, "GLOBAL");

        ratio = totalWins / Math.max(totalLosses, 1);
        statisticsMap.get(uuid).get("GLOBAL").put(Statistic.WLR, ratio);
    }

    private void recalculateKDR(UUID uuid, GameMode gameMode) {
        double totalKills = getStat(uuid, Statistic.KILLS, gameMode.getId());
        double totalDeaths = getStat(uuid, Statistic.DEATHS, gameMode.getId());

        double ratio = totalKills / Math.max(totalDeaths, 1);
        statisticsMap.get(uuid).get(gameMode.getId()).put(Statistic.KDR, ratio);

        totalKills = getStat(uuid, Statistic.KILLS, "GLOBAL");
        totalDeaths = getStat(uuid, Statistic.DEATHS, "GLOBAL");

        ratio = totalKills / Math.max(totalDeaths, 1);
        statisticsMap.get(uuid).get("GLOBAL").put(Statistic.KDR, ratio);
    }

    private void incrementEntry(UUID uuid, String primaryKey, Statistic statistic) {
        Map<Statistic, Double> subMap = statisticsMap.get(uuid).get(primaryKey);
        subMap.put(statistic, subMap.getOrDefault(statistic, 0D) + 1);
    }

    public double getStat(UUID uuid, Statistic statistic, String gameMode) {
        return Objects.firstNonNull(statisticsMap.getOrDefault(uuid, ImmutableMap.of()).getOrDefault(gameMode, ImmutableMap.of()).get(statistic), 0D);
    }

    private enum Statistic {
        WINS, LOSSES, WLR, KILLS, DEATHS, KDR
    }

    
}
