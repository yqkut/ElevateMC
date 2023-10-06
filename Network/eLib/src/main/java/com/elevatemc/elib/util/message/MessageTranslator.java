package com.elevatemc.elib.util.message;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

import java.util.ArrayList;
import java.util.List;

public final class MessageTranslator {

    private static final CharSet colorCodes = new CharOpenHashSet("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".toCharArray());

    public static String translate(String text) {
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length - 1; ++i) {
            if (chars[i] == '&' && colorCodes.contains(chars[i + 1])) {
                chars[i] = MessageConstants.COLOR_SYMBOL;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    public static List<String> translateLines(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(translate(line));
        }

        return toReturn;
    }
}
