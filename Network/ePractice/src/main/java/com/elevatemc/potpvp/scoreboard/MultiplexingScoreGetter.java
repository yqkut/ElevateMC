package com.elevatemc.potpvp.scoreboard;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.scoreboard.construct.ScoreGetter;
import com.elevatemc.elib.util.Pair;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.queue.MatchQueue;
import com.elevatemc.potpvp.queue.MatchQueueEntry;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.elib.util.UUIDUtils;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, LinkedList<String>> matchScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> lobbyScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> gameScoreGetter;

    MultiplexingScoreGetter(
        BiConsumer<Player, LinkedList<String>> matchScoreGetter,
        BiConsumer<Player, LinkedList<String>> lobbyScoreGetter,
        BiConsumer<Player, LinkedList<String>> gameScoreGetter
    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.lobbyScoreGetter = lobbyScoreGetter;
        this.gameScoreGetter = gameScoreGetter;
    }

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        if (PotPvPSI.getInstance() == null) return;
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
            if (matchHandler.isPlayingOrSpectatingMatch(player) && !matchHandler.isPlayingEvent(player)) {
                matchScoreGetter.accept(player, scores);
            } else {
                final Game game = GameQueue.INSTANCE.getCurrentGame(player);
                if(game != null && game.getPlayers().contains(player) && game.getState() != GameState.ENDED) {
                    gameScoreGetter.accept(player, scores);
                } else {
                    lobbyScoreGetter.accept(player, scores);
                }
            }

            Optional<UUID> followingOpt = PotPvPSI.getInstance().getFollowHandler().getFollowing(player);
            if (followingOpt.isPresent()) {
                scores.add("");
                scores.add(style.getValue() + "Following&7: " + style.getKey() + UUIDUtils.name(followingOpt.get()));
                if (player.hasPermission("core.staffteam")) {
                    Player following = Bukkit.getPlayer(followingOpt.get());
                    MatchQueueEntry targetEntry = getQueueEntry(following);

                    if (targetEntry != null) {
                        MatchQueue queue = targetEntry.getQueue();

                        scores.add(style.getValue() + "Target's Queue&7: " + style.getKey() + queue.getGameMode().getName() + (queue.isCompetitive() ? "&7(R)" : "&7(C)"));
                    }
                }
            }

            if (eLib.getInstance().getAutoRebootHandler().isRebooting()) {
                String secondsStr = TimeUtils.formatIntoMMSS(eLib.getInstance().getAutoRebootHandler().getRebootSecondsRemaining());
                scores.add("&4&lReboot&7: &c" + secondsStr);
            }


            if (player.hasMetadata("modmode")) {
                scores.add("");
                scores.add("&c&oStaff Mode");
            }

            if (scores.size() <= 13) {
                scores.add("");
            }

            if (scores.size() <= 13) {
                scores.add(style.getKey() + "      elevatemc.com     ");
            }

            if (scores.size() <= 13) {
                scores.addFirst("");
            }

            if (scores.size() <= 13) {
                scores.addFirst( ChatColor.GRAY.toString() + ChatColor.ITALIC + "          " + PotPvPScoreboardConfiguration.date + "       ");
            }
        }

        return;
    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }

}