package com.elevatemc.potpvp.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static String translate(String toTranslate) {
        return ChatColor.translateAlternateColorCodes('&', toTranslate);
    }

    public static List<String> translate(List<String> toTranslate) {
        return toTranslate.stream().map(Color::translate).collect(Collectors.toList());
    }

}
