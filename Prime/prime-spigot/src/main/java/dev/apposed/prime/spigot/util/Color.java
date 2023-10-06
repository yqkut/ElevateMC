package dev.apposed.prime.spigot.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }

    public static final String SPACER_SHORT = translate("&7&m--------------------"); // Menus, Scoreboard
    public static final String SPACER_LONG = translate("&8&m----------------------------------------"); // Chat
}