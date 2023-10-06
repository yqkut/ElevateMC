package com.elevatemc.elib.util.cosmetics;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class ArmorUtil {

    public static final List<Color> COLORS = new ArrayList<>();

    static {
        for (int r = 0; r < 100; r++) COLORS.add(Color.fromRGB(r * 255 / 100, 255, 0));
        for (int g = 100; g > 0; g--) COLORS.add(Color.fromRGB(255, g * 255 / 100, 0));
        for (int b = 0; b < 100; b++) COLORS.add(Color.fromRGB(255, 0, b * 255 / 100));
        for (int r = 100; r > 0; r--) COLORS.add(Color.fromRGB(r * 255 / 100, 0, 255));
        for (int g = 0; g < 100; g++) COLORS.add(Color.fromRGB(0, g * 255 / 100, 255));
        for (int b = 100; b > 0; b--) COLORS.add(Color.fromRGB(0, 255, b * 255 / 100));

        COLORS.add(Color.fromRGB(0, 255, 0));
    }

    public static int parseColor(Color color) {
        if (color.equals(Color.YELLOW) || color.equals(Color.OLIVE)) return 4;
        if (color.equals(Color.BLUE) || color.equals(Color.NAVY)) return 11;
        if (color.equals(Color.GREEN)) return 13;
        if (color.equals(Color.LIME)) return 5;
        if (color.equals(Color.RED) || color.equals(Color.MAROON)) return 14;
        if (color.equals(Color.SILVER)) return 8;
        if (color.equals(Color.GRAY)) return 7;
        if (color.equals(Color.AQUA)) return 3;
        if (color.equals(Color.TEAL)) return 9;
        if (color.equals(Color.PURPLE)) return 2;
        if (color.equals(Color.FUCHSIA)) return 6;
        if (color.equals(Color.ORANGE)) return 1;
        if (color.equals(Color.BLACK)) return 15;

        return -1;
    }
}
