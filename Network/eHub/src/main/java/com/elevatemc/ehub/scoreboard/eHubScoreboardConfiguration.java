package com.elevatemc.ehub.scoreboard;

import com.elevatemc.ehub.eHub;
import com.elevatemc.elib.scoreboard.config.ScoreboardConfiguration;
import com.elevatemc.elib.scoreboard.construct.TitleGetter;
import com.elevatemc.elib.util.Pair;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import dev.apposed.prime.spigot.util.Color;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class eHubScoreboardConfiguration {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
    public static String date;
    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        Bukkit.getScheduler().runTaskTimerAsynchronously(eHub.getInstance(), () -> {
            date = dateFormat.format(new Date());
        }, 0L, 20 * 60 * 5);

        configuration.setTitleGetter(new TitleGetter() {
            @Override
            public String getTitle(Player player) {
                final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
                return Color.translate(style.getKey().toString() + ChatColor.BOLD + "Elevate &7" + "❘" + style.getValue() + " Hub");
            }
        });

        configuration.setScoreGetter(new eHubScoreGetter());

        return (configuration);
    }

}