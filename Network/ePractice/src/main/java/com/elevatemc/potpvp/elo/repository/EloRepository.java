package com.elevatemc.potpvp.elo.repository;

import com.elevatemc.potpvp.gamemode.GameMode;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EloRepository {

    Map<GameMode, Integer> loadElo(Set<UUID> playerUuids) throws IOException;
    void saveElo(Set<UUID> playerUuids, Map<GameMode, Integer> elo) throws IOException;

   Map<String, Integer> topElo(GameMode gameMode) throws IOException;
}