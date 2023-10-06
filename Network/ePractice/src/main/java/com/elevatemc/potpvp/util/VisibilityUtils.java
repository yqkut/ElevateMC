package com.elevatemc.potpvp.util;

import com.elevatemc.potpvp.hctranked.game.RankedGame;
import com.elevatemc.potpvp.hctranked.game.RankedGameHandler;
import com.elevatemc.potpvp.setting.Setting;
import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibilityFlicker(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(target);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> updateVisibility(target), 10L);
    }

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
            }
        }
    }

    private static boolean shouldSeePlayer(Player viewer, Player target) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        RankedGameHandler rankedGameHandler = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            // we're not in a match so we hide other players based on their party/match
            Party targetParty = partyHandler.getParty(target);
            RankedGame rankedGame = rankedGameHandler.getJoinedGame(viewer);
            Optional<UUID> following = followHandler.getFollowing(viewer);

            boolean viewerPlayingMatch = matchHandler.isPlayingOrSpectatingMatch(viewer);
            boolean viewerSameParty = targetParty != null && targetParty.isMember(viewer.getUniqueId());
            boolean viewerSameRankedGame = rankedGame != null && rankedGame.getJoinedPlayers().contains(target.getUniqueId());
            boolean viewerFollowingTarget = following.isPresent() && following.get().equals(target.getUniqueId());

            boolean targetLobbyVisibility = target.hasPermission("potpvp.lobby.visibility");
            boolean targetNotVanishedAndVisible = !target.hasMetadata("modmode") && targetLobbyVisibility;

            return viewerPlayingMatch || viewerSameParty || viewerFollowingTarget || viewerSameRankedGame || targetNotVanishedAndVisible;
        } else {
            // we're in a match so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
            boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
            boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer);

            return !targetIsSpectator || (viewerSpecSetting && viewerIsSpectator && !target.hasMetadata("modmode"));
        }
    }

}