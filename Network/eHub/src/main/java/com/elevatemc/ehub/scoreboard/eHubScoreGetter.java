package com.elevatemc.ehub.scoreboard;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.queue.QueueHandler;
import com.elevatemc.ehub.queue.QueuePosition;
import com.elevatemc.elib.autoreboot.AutoRebootHandler;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.scoreboard.construct.ScoreGetter;
import com.elevatemc.elib.util.Pair;
import com.elevatemc.elib.util.TimeUtils;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

public class eHubScoreGetter implements ScoreGetter {

    private static final ProfileHandler profileHandler = eHub.getInstance().getPrime().getModuleHandler().getModule(ProfileHandler.class);

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        Profile profile = profileHandler.getCache().getIfPresent(player.getUniqueId());
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);

        if (profile != null) {
            if (profile.hasActivePunishment(PunishmentType.BAN) || profile.hasActivePunishment(PunishmentType.BLACKLIST)|| player.hasMetadata("related")) {
                scores.add("&4&l✘ You are banned ✘");
                scores.add("&4Type /register to appeal");
            } else {
                RadioSongPlayer radioSongPlayer = eHub.getInstance().getRadioSongPlayer();
                QueueHandler queueHandler = eHub.getInstance().getQueueHandler();
                scores.add(style.getKey().toString() + ChatColor.BOLD + "Info:");
                scores.add(style.getKey() + "┃ " + style.getValue()  + "Rank: " + profile.getHighestActiveNonHiddenGrant().getRank().getColoredDisplay());
                scores.add(style.getKey() + "┃ " + style.getValue()  + "Global: " + style.getKey() + eHub.getInstance().getGlobalPlayerCount());
                scores.add(" ");
                scores.add(style.getKey().toString() + ChatColor.BOLD + "Servers:");
                scores.add(style.getKey() + "┃ " + style.getValue()  + "Practice: " + style.getKey() + eHub.getInstance().getServerPlayerCount("Elevate-Practice") + "/" + eHub.getInstance().getMaxPlayerCount("Elevate-Practice"));

                if (radioSongPlayer.getPlayerUUIDs().contains(player.getUniqueId())) {
                    Song song = radioSongPlayer.getSong();
                    String title = song.getTitle();
                    if (title.length() > 30) {
                        title = title.substring(0, 30);
                    }
                    scores.add(" ");
                    scores.add(style.getKey().toString() + ChatColor.BOLD + "Music:");
                    scores.add(style.getKey() + "┃ " + style.getValue()  + "Title: " + style.getKey() + title);
                }

                if (queueHandler.getPositions().containsKey(player.getUniqueId())) {
                    scores.add(" ");
                    QueuePosition queuePosition = queueHandler.getPositions().get(player.getUniqueId());
                    scores.add(style.getKey().toString() + ChatColor.BOLD + "Queue:");
                    scores.add(style.getKey() + "┃&r " + style.getValue()  + queuePosition.getServer() + ": " + style.getKey() +  queuePosition.getPosition() + "/" + queuePosition.getTotal());
                }
            }
        } else {
            scores.add("&7Loading...");
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
            scores.addFirst( ChatColor.GRAY.toString() + ChatColor.ITALIC + "          " + eHubScoreboardConfiguration.date + "       ");
        }
    }
}
