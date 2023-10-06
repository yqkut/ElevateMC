package com.elevatemc.potpvp.validation;

import com.elevatemc.potpvp.tournament.Tournament;
import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@UtilityClass
public final class PotPvPValidation {

    private static final String CANNOT_DUEL_SELF = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't duel yourself!";
    private static final String CANNOT_DUEL_OWN_PARTY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't duel your own party!";

    private static final String CANNOT_DO_THIS_WHILE_IN_RANKED_GAME = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while in a ranked game!";
    private static final String CANNOT_DO_THIS_WHILE_IN_PARTY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while in a party!";
    private static final String CANNOT_DO_THIS_WHILE_QUEUED = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while queued!";
    private static final String CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while you're not in the lobby!";
    private static final String CANNOT_DO_THIS_WHILE_IN_MATCH = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while participating in or spectating a match!";
    private static final String CANNOT_DO_THIS_WHILE_IN_EVENT = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do this while participating in or spectating an event!";
    private static final String CANNOT_DO_THIS_WHILE_FOLLOWING = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot do this while following someone! Type /unfollow to exit.";
    private static final String CANNOT_DO_THIS_IN_SILENT_MODE = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot do this while in staff mode!";
    private static final String CANNOT_DO_THIS_WHILST_IN_TOURNAMENT = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot do this whilst in the tournament!";
    private static final String CANNOT_DO_THIS_TOO_FEW_PEOPLE = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need more members to do that!";

    private static final String TARGET_PARTY_NOT_IN_LOBBY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That party is not in the lobby!";
    private static final String TARGET_PLAYER_NOT_IN_LOBBY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That player is not in the lobby!";
    private static final String TARGET_PLAYER_FOLLOWING_SOMEONE = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That player is currently following someone!";
    private static final String TARGET_PLAYER_HAS_DUELS_DISABLED = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The player has duels disabled!";
    private static final String TARGET_IN_PARTY = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That player is in a party!";
    private static final String TARGET_PARTY_HAS_DUELS_DISABLED = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The party has duels disabled!";
    private static final String TARGET_PARTY_REACHED_MAXIMUM_SIZE = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The party is full.";
    private static final String TARGET_PARTY_REACHED_MAXIMUM_SIZE_FOR_TOURNAMENT = ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That party is full for the tournament.";

    public static boolean canJoinRankedGame(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (isInMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        return true;
    }

    public static boolean canJoinRankedGameSilent(Player player) {
        if (isInParty(player)) {
            return false;
        }

        if (!isInLobby(player)) {
            return false;
        }

        if (isFollowingSomeone(player)) {
            return false;
        }

        if (isInMatch(player)) {
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInQueue(player)) {
            return false;
        }

        return true;
    }

    public static boolean canSendDuel(Player sender, Player target) {
        if (isInRankedGame(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (isInSilentMode(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (sender == target) {
            sender.sendMessage(CANNOT_DUEL_SELF);
            return false;
        }

        if (!isInLobby(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (!isInLobby(target)) {
            sender.sendMessage(TARGET_PLAYER_NOT_IN_LOBBY);
            return false;
        }

        if (isFollowingSomeone(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (!getSetting(target, Setting.RECEIVE_DUELS)) {
            sender.sendMessage(TARGET_PLAYER_HAS_DUELS_DISABLED);
            return false;
        }

        return true;
    }

    // sender = the one who typed /accept
    public static boolean canAcceptDuel(Player sender, Player duelSentBy) {
        if (isInSilentMode(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (!isInLobby(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if (!isInLobby(duelSentBy)) {
            sender.sendMessage(TARGET_PLAYER_NOT_IN_LOBBY);
            return false;
        }

        if (isFollowingSomeone(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (isFollowingSomeone(duelSentBy)) {
            sender.sendMessage(TARGET_PLAYER_FOLLOWING_SOMEONE);
            return false;
        }

        if(isInEvent(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInParty(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInParty(duelSentBy)) {
            sender.sendMessage(TARGET_IN_PARTY);
            return false;
        }

        return true;
    }

    public static boolean canSendDuel(Party sender, Party target, Player initiator) {
        if (isInRankedGame(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (isInTournament(sender)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        if (sender == target) {
            initiator.sendMessage(CANNOT_DUEL_OWN_PARTY);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if (!isInLobby(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PARTY_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (!getSetting(Bukkit.getPlayer(target.getLeader()), Setting.RECEIVE_DUELS)) {
            initiator.sendMessage(TARGET_PARTY_HAS_DUELS_DISABLED);
            return false;
        }

        /*if (isInTournament(sender)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }*/

        return true;
    }

    public static boolean canAcceptDuel(Party target, Party sender, Player initiator) {
        if (isInRankedGame(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (!isInLobby(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PLAYER_NOT_IN_LOBBY);
            return false;
        }

        /*if (isInTournament(target)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }*/

        return true;
    }

    public static boolean canJoinParty(Player player, Party party) {
        if (isInRankedGame(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (party.getMembers().size() >= Party.MAX_SIZE && !Bukkit.getPlayer(party.getLeader()).isOp()) {
            player.sendMessage(TARGET_PARTY_REACHED_MAXIMUM_SIZE);
            return false;
        }

        Tournament tournament = PotPvPSI.getInstance().getTournamentHandler().getTournament();
        if (tournament != null && isInTournament(party) && party.getMembers().size() >= tournament.getRequiredPartySize()) {
            player.sendMessage(TARGET_PARTY_REACHED_MAXIMUM_SIZE_FOR_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canUseSpectateItem(Player player) {
        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        return canUseSpectateItemIgnoreMatchSpectating(player);
    }

    public static boolean canUseSpectateItemIgnoreMatchSpectating(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInRankedGame(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        return true;
    }

    public static boolean canFollowSomeone(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInRankedGame(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }

        if (!(isInLobby(player))) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't do that here!");
            return false;
        }

        return isInLobby(player);
    }

    public static boolean canJoinQueue(Player player) {
        if (isInRankedGame(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_RANKED_GAME);
            return false;
        }
        if (isInSilentMode(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        return true;
    }

    public static boolean canJoinQueue(Party party) {
        if (isInQueue(party)) {
            // we shouldn't really message the whole party
            // here, but players should never really be able to click
            // this item while in a queue anyway (and it takes a lot of work
            // to rework this validation to include an initiator)
            party.message(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInTournament(party)) {
            party.message(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canStartTeamSplit(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInTournament(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        if (party.getMembers().size() < 2) {
            initiator.sendMessage(CANNOT_DO_THIS_TOO_FEW_PEOPLE);
            return false;
        }

        return true;
    }

    public static boolean canStartFfa(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_LOBBY);
            return false;
        }

        if(isInEvent(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_EVENT);
            return false;
        }

        if (isInTournament(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        if (party.getMembers().size() < 2) {
            initiator.sendMessage(CANNOT_DO_THIS_TOO_FEW_PEOPLE);
            return false;
        }

        return true;
    }

    private static boolean getSetting(Player player, Setting setting) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        return settingHandler.getSetting(player, setting);
    }

    public static boolean isInRankedGame (Player player) {
        return PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player) != null;
    }

    private static boolean isInParty(Player player) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        return partyHandler.hasParty(player);
    }

    private static boolean isInQueue(Player player) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        return queueHandler.isQueued(player.getUniqueId());
    }

    private static boolean isInQueue(Party party) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        return queueHandler.isQueued(party);
    }

    private boolean isInMatch(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        return matchHandler.isPlayingMatch(player);
    }

    private boolean isInLobby(Player player) {
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        return lobbyHandler.isInLobby(player);
    }

    private boolean isInEvent(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        return matchHandler.isPlayingEvent(player);
    }

    private boolean isFollowingSomeone(Player player) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        return followHandler.getFollowing(player).isPresent();
    }

    private boolean isInTournament(Party party) {
        return PotPvPSI.getInstance().getTournamentHandler().isInTournament(party);
    }

    private boolean isInSilentMode(Player player) {
        return player.hasMetadata("modmode");
    }

}
