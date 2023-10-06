package com.elevatemc.potpvp.elo;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.listener.EloLoadListener;
import com.elevatemc.potpvp.elo.listener.EloUpdateListener;
import com.elevatemc.potpvp.elo.repository.EloRepository;
import com.elevatemc.potpvp.elo.repository.MongoEloRepository;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EloHandler {

    public static final int DEFAULT_ELO = 1000;

    private final Map<Set<UUID>, Map<GameMode, Integer>> eloData = new ConcurrentHashMap<>();
    @Getter private final EloRepository eloRepository;

    public EloHandler() {
        Bukkit.getPluginManager().registerEvents(new EloLoadListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new EloUpdateListener(this, new EloCalculator(
            35, // k power
            7,
            25,
            7,
            25
        )), PotPvPSI.getInstance());

        eloRepository = new MongoEloRepository();
    }

    public int getElo(Player player, GameMode gameMode) {
        return getElo(ImmutableSet.of(player.getUniqueId()), gameMode);
    }

    public void setElo(Player player, GameMode gameMode, int newElo) {
        setElo(ImmutableSet.of(player.getUniqueId()), gameMode, newElo);
    }

    public int getElo(Set<UUID> playerUuids, GameMode gameMode) {
        Map<GameMode, Integer> partyElo = eloData.getOrDefault(playerUuids, ImmutableMap.of());
        return partyElo.getOrDefault(gameMode, DEFAULT_ELO);
    }

    public int getGlobalElo(UUID uuid) {
        Map<GameMode, Integer> eloValues = eloData.getOrDefault(ImmutableSet.of(uuid), ImmutableMap.of());
        if (eloValues.isEmpty()) return EloHandler.DEFAULT_ELO;
        int[] wrapper = new int[2];
        GameMode.getAll().stream().filter(GameMode::getSupportsCompetitive).forEach(gameMode -> {
            wrapper[0] = wrapper[0] + 1;
            wrapper[1] = wrapper[1] + eloValues.getOrDefault(gameMode, EloHandler.DEFAULT_ELO);
        });

        return wrapper[1] / wrapper[0];
    }

    public void setElo(Set<UUID> playerUuids, GameMode gameMode, int newElo) {
        Map<GameMode, Integer> partyElo = eloData.computeIfAbsent(playerUuids, i -> new ConcurrentHashMap<>());
        partyElo.put(gameMode, newElo);

        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            try {
                eloRepository.saveElo(playerUuids, partyElo);
            } catch (IOException ex) {
                // just log, nothing else to do.
                ex.printStackTrace();
            }
        });
    }

    public void loadElo(Set<UUID> playerUuids) {
        Map<GameMode, Integer> partyElo;

        try {
            partyElo = new ConcurrentHashMap<>(eloRepository.loadElo(playerUuids));
        } catch (IOException ex) {
            // just print + return an empty map, this will cause us
            // to fall back to default values.
            ex.printStackTrace();
            partyElo = new ConcurrentHashMap<>();
        }

        eloData.put(playerUuids, partyElo);
    }

    public void unloadElo(Set<UUID> playerUuids) {
        eloData.remove(playerUuids);
    }

    public Map<String, Integer> topElo(GameMode type) {
        Map<String, Integer> topElo;

        try {
            topElo = eloRepository.topElo(type);
        } catch (IOException ex) {
            ex.printStackTrace();
            topElo = ImmutableMap.of();
        }

        return topElo;
    }

    public void resetElo(final UUID player) {
        Bukkit.getLogger().info("Resetting elo of " + UUIDUtils.name(player) + ".");
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            unloadElo(ImmutableSet.of(player));
            try {
                eloRepository.saveElo(ImmutableSet.of(player), ImmutableMap.of());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}