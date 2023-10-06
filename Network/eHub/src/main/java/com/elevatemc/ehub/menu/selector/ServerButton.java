package com.elevatemc.ehub.menu.selector;

import com.elevatemc.ehub.eHub;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Pair;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class ServerButton extends Button {

    private final Material material;
    private final String name;
    private final String server;
    private final String region;
    private final List<String> description;

    @Override
    public String getName(Player player) {
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        return style.getKey().toString() + ChatColor.BOLD + name;
    }

    @Override
    public List<String> getDescription(Player player) {
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        List<String> meta = new ArrayList<>();

        meta.add("");
        meta.add(style.getKey() + "┃ " + style.getValue()  + "Playing: " + style.getKey() + eHub.getInstance().getServerPlayerCount("Elevate-Practice"));
        meta.add(style.getKey() + "┃ " + style.getValue()  + "Region: " + style.getKey() + region);

        return Stream.concat(description.stream(), meta.stream())
                .collect(Collectors.toList());
    }

    @Override
    public Material getMaterial(Player player) {
        return material;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        eHub.getInstance().getQueueHandler().joinQueue(player, server);
    }
}