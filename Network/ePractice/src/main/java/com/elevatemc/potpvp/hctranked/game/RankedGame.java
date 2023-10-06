package com.elevatemc.potpvp.hctranked.game;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.hctranked.packet.StartGamePacket;
import com.elevatemc.potpvp.hctranked.packet.WinGamePacket;
import com.elevatemc.potpvp.lobby.LobbyUtils;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.VisibilityUtils;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RankedGame {
    @Getter
    private final Set<UUID> allPlayers;

    @Getter
    private final Set<UUID> joinedPlayers;

    @Getter
    private String arena;

    @Getter
    private final RankedGameTeam team1;

    @Getter
    private final RankedGameTeam team2;

    @Getter
    private RankedGameState state;

    @Getter
    private final String gameId;

    @Getter
    private String matchId = null;

    public RankedGame(String gameId, Set<UUID> players, RankedGameTeam team1, RankedGameTeam team2, String arena) {
        this.gameId = gameId;
        this.allPlayers = players;
        this.joinedPlayers = new HashSet<>();
        this.team1 = team1;
        this.team2 = team2;
        this.arena = arena;
        this.state = RankedGameState.WAITING;
    }

    public RankedGameTeam getTeam(Player player) {
        if (team1.getPlayers().contains(player.getUniqueId())) {
            return team1;
        } else if (team2.getPlayers().contains(player.getUniqueId())) {
            return team2;
        }
        return null;
    }

    public void join(Player player) {
        getJoinedPlayers().add(player.getUniqueId());
        getTeam(player).getJoinedPlayers().add(player.getUniqueId());
        InventoryUtils.resetInventoryDelayed(player);
        Bukkit.getLogger().info("RankedHCF > Joined game Id: " + gameId + " Player: " + player.getName());
        messageJoined(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has joined your ranked game.");
        player.sendMessage(ChatColor.GREEN + "You joined the ranked game!");
        getAllPlayers().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                VisibilityUtils.updateVisibility(p);
            }
        });
    }

    public void leave(Player player) {
        getJoinedPlayers().remove(player.getUniqueId());
        if (state.equals(RankedGameState.WAITING)) {
            RankedGameTeam team = getTeam(player);
            team.getJoinedPlayers().remove(player.getUniqueId());
            if (getState().equals(RankedGameState.WAITING) && team.isReady()) {
                team.setReady(false);
                if (team.getJoinedPlayers().contains(team.getCaptain())) {
                    Player captain = Bukkit.getPlayer(team.getCaptain());
                    if (captain != null) LobbyUtils.resetInventory(captain);
                }
            }

            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            Bukkit.getLogger().info("RankedHCF > Left game Id: " + gameId + " Player: " + player.getName());
            messageJoined(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has left your ranked game.");
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You left the ranked game!");
        }

    }

    public void checkStart() {
        if (team1.isReady() && team2.isReady()) {
            start();
        }
    }

    private void start() {
        this.state = RankedGameState.IN_PROGRESS;
        Match match = PotPvPSI.getInstance().getMatchHandler().startMatch(
                ImmutableList.of(new MatchTeam(team1.getJoinedPlayers()), new MatchTeam(team2.getJoinedPlayers())),
                GameModes.TEAMFIGHT,
                arena,
                false,
                false
        );
        matchId = match.get_id();
        Bukkit.getLogger().info("RankedHCF > Game started Id: " + gameId + " Map: " + arena);
        eLib.getInstance().getPidginHandler().sendPacket(new StartGamePacket(gameId, matchId));
    }

    public void end(int winnerTeam) {
        Bukkit.getLogger().info("RankedHCF > Game ended Id: " + gameId + " Winner Team: " + winnerTeam);
        eLib.getInstance().getPidginHandler().sendPacket(new WinGamePacket(gameId, winnerTeam));
        PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().removeGame(this);
    }

    public void messageJoined(String message) {
        for (UUID pl : joinedPlayers) {
            Player player = Bukkit.getPlayer(pl);

            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

}
