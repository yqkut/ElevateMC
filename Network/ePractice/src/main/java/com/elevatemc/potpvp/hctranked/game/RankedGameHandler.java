package com.elevatemc.potpvp.hctranked.game;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.hctranked.game.listener.RankedGameListener;
import com.elevatemc.potpvp.hctranked.game.listener.RankedItemListener;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RankedGameHandler {
    @Getter
    private final Set<RankedGame> rankedGames = new HashSet<>();

    public RankedGameHandler() {
        Bukkit.getPluginManager().registerEvents(new RankedGameListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new RankedItemListener(), PotPvPSI.getInstance());
    }

    public void createGame(String gameId, Set<UUID> players, RankedGameTeam team1, RankedGameTeam team2, String map) {
        Bukkit.getLogger().info("RankedHCF > Created a new game Id: " + gameId + " Size: " + players.size() + " Map: " + map);
        RankedGame game = new RankedGame(gameId, players, team1, team2, map);
        rankedGames.add(game);
        players.forEach(pl -> {
            Player player = Bukkit.getPlayer(pl);
            if (player != null && PotPvPValidation.canJoinRankedGameSilent(player)) {
                game.join(player);
            }
        });
    }

    public RankedGame getInvitedGame(Player player) {
        UUID uuid = player.getUniqueId();
        return rankedGames.stream().filter(g -> g.getAllPlayers().contains(uuid) && !g.getJoinedPlayers().contains(uuid) && g.getState().equals(RankedGameState.WAITING)).findFirst().orElse(null);
    }

    public RankedGame getJoinedGame(Player player) {
        UUID uuid = player.getUniqueId();
        return rankedGames.stream().filter(g -> g.getJoinedPlayers().contains(uuid)).findFirst().orElse(null);
    }

    public RankedGame getGameByMatchId(String matchId) {
        return rankedGames.stream().filter(g -> {
            if (g.getMatchId() != null) {
                return g.getMatchId().equals(matchId);
            } else {
                return false;
            }
        }).findFirst().orElse(null);
    }

    public void voidGame(String gameId) {
        RankedGame game = rankedGames.stream().filter(g -> g.getGameId().equals(gameId)).findFirst().orElse(null);
        if (game == null) return;
        Bukkit.getLogger().info("RankedHCF > Voided game Id: " + gameId + " Match: " + game.getMatchId());
        game.messageJoined(ChatColor.RED + "This game has been voided. You will not gain or lose any elo out of this. If you wish to play again please requeue.");
        removeGame(game);
        switch (game.getState()) {
            case WAITING:
                for (UUID pl : game.getJoinedPlayers()) {
                    Player player = Bukkit.getPlayer(pl);

                    if (player != null) {
                        InventoryUtils.resetInventoryNow(player);
                    }
                }
            case IN_PROGRESS:
                Match match = PotPvPSI.getInstance().getMatchHandler().getHostedMatches().stream().filter(m -> m.get_id().equals(game.getMatchId())).findFirst().orElse(null);
                if (match != null) {
                    match.getTeams().get(0).getAliveMembers().forEach(p -> {
                        Player player = Bukkit.getPlayer(p);
                        if (player != null) {
                            player.setHealth(0);
                        }
                    });
                }
        }
    }

    public void removeGame(RankedGame game) {
        rankedGames.remove(game);
    }
}
