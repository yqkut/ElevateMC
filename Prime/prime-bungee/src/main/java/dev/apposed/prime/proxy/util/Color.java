package dev.apposed.prime.proxy.util;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }
}