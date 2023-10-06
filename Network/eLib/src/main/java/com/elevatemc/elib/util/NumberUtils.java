package com.elevatemc.elib.util;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.TreeMap;

public class NumberUtils {

    public static TreeMap<Integer, String> map = new TreeMap<>();
    private static final NumberFormat BALANCE_FORMAT = NumberFormat.getInstance();

    static {
        map.put(1, "I");
        map.put(4, "IV");
        map.put(5, "V");
        map.put(9, "IX");
        map.put(10, "X");
        map.put(40, "XL");
        map.put(50, "L");
        map.put(90, "XC");
        map.put(100, "C");
        map.put(400, "CD");
        map.put(500, "D");
        map.put(900, "CM");
        map.put(1000, "M");
    }
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static String formatBalance(BigInteger balance) {
        return BALANCE_FORMAT.format(balance);
    }

    public static String formatBalance(int balance) {
        return BALANCE_FORMAT.format((long)balance);
    }
    public static String formatBalance(double balance) {
        return BALANCE_FORMAT.format((long)balance);
    }
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    static {
        BALANCE_FORMAT.setGroupingUsed(true);
    }
}
