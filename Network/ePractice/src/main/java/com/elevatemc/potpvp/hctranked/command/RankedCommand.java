package com.elevatemc.potpvp.hctranked.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.lobby.LobbyUtils;
import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import com.elevatemc.potpvp.hctranked.game.RankedGameState;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RankedCommand {
    @Command(names = {"ranked"}, permission = "")
    public static void join(Player sender) {
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getInvitedGame(sender);
        if (!PotPvPValidation.canJoinRankedGame(sender)) {
            return;
        }
        if (game == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no game for you to join.");
            return;
        }

        if (game.getState() != RankedGameState.WAITING) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "This game is already in progress. Please report this to an admin.");
            return;
        }

        game.join(sender);
    }

    @Command(names = {"ranked leave"}, permission = "")
    public static void leave(Player sender) {
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(sender);
        if (game == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no game for you to leave.");
            return;
        }

        game.leave(sender);
    }

    @Command(names = {"ranked list"}, permission = "rankedhcf.admin")
    public static void list(Player sender) {
        Set<RankedGame> games = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getRankedGames();
        for (RankedGame game : games) {
            sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Game " + game.getGameId());
            sender.sendMessage("Match: " + ChatColor.DARK_AQUA + game.getMatchId());
            sender.sendMessage("Map: " + ChatColor.DARK_AQUA + game.getArena());
            sender.sendMessage("State: " + ChatColor.DARK_AQUA + game.getState());
            sender.sendMessage("Joined: " + ChatColor.DARK_AQUA + game.getJoinedPlayers().size() + "/" +  game.getAllPlayers().size());
        }
    }

    @Command(names = {"ranked void"}, permission = "rankedhcf.admin")
    public static void voidGame(Player sender, @Parameter(name = "game") String text) {
        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "If there was a game with that id it is voided.");
        PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().voidGame(text);
    }

    @Command(names = {"ranked info"}, permission = "")
    public static void info(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player);
        if (game == null) {
            if (sender.getUniqueId().equals(player.getUniqueId())) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You are not in a game");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That player is not in a game.");
            }
            return;
        }

        Set<UUID> joinedPlayers = game.getJoinedPlayers();
        RankedGameTeam team1 = game.getTeam1();
        RankedGameTeam team2 = game.getTeam2();
        String team1Online = team1.getPlayers().stream().filter(m -> !team1.getCaptain().equals(m)).map(m -> joinedPlayers.contains(m) ? "&a" + UUIDUtils.name(m) : "&7" + UUIDUtils.name(m)).collect(Collectors.joining(", "));
        String team2Online = team2.getPlayers().stream().filter(m -> !team2.getCaptain().equals(m)).map(m -> joinedPlayers.contains(m) ? "&a" + UUIDUtils.name(m) : "&7" + UUIDUtils.name(m)).collect(Collectors.joining(", "));

        UUID team1Captain = team1.getCaptain();
        UUID team2Captain = team2.getCaptain();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Team 1 &7[" + team1.getJoinedPlayers().size() + "/" + team1.getPlayers().size() + "]"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Captain&7: " + (joinedPlayers.contains(team1Captain) ? "&a"+ UUIDUtils.name(team1Captain) : "&7" + UUIDUtils.name(team1Captain))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Members&7: " + team1Online));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Ready&7: " + (game.getTeam1().isReady() ? "&aYes" : "&cNo")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Team 2 &7[" + team2.getJoinedPlayers().size() + "/" + team2.getPlayers().size() + "]"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Captain&7: " + (joinedPlayers.contains(team2Captain) ? "&a" + UUIDUtils.name(team2Captain) : "&7" + UUIDUtils.name(team2Captain))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Members&7: " + team2Online));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- Ready&7: " + (game.getTeam2().isReady() ? "&aYes" : "&cNo")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
    }

    private static final Map<UUID, Long> toggleReady = new HashMap<>();

    @Command(names = {"ready"}, permission = "")
    public static void ready(Player sender) {
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(sender);
        if (game == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You are not in a ranked game.");
            return;
        }

        RankedGameTeam team = game.getTeam(sender);
        if (!team.getCaptain().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must be a captain to ready up.");
            return;
        }

        boolean ready = !team.isReady();

        if (ready && team.getJoinedPlayers().size() != team.getPlayers().size()) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Not everyone of your team joined the ranked game yet.");
            return;
        }

        Collection<PvPClasses> kits = team.getKits().values();
        int bards = Collections.frequency(kits, PvPClasses.BARD);
        int archers = Collections.frequency(kits, PvPClasses.ARCHER);

        /* if (bards < 1) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need a bard before you ready up.");
            return;
        }

        if (archers < 1) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need an archer before you ready up.");
            return;
        } */

        boolean togglePermitted = toggleReady.getOrDefault(sender.getUniqueId(), 0L) < System.currentTimeMillis();

        if (!togglePermitted) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Please wait before doing this again!");
            return;
        }

        toggleReady.put(sender.getUniqueId(), System.currentTimeMillis() + 3_000L);
        team.setReady(ready);
        LobbyUtils.resetInventory(sender);

        game.checkStart();
    }
}