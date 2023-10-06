package dev.apposed.prime.proxy.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static TextComponent translate(String text) {
        return LegacyComponentSerializer.legacy('&').deserialize(text);
    }

    public static List<TextComponent> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }

    public static String translateNormal(String text){
        return translate(text).content();
    }
}