package dev.apposed.prime.proxy.util.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {

    public static String formatIntoDetailedString(int secs) {
        if (secs == 0) {
            return "0 seconds";
        } else {
            int remainder = secs % 86400;
            int days = secs / 86400;
            int hours = remainder / 3600;
            int minutes = remainder / 60 - hours * 60;
            int seconds = remainder % 3600 - minutes * 60;
            String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
            String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
            String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
            String fSeconds = seconds > 0 ? " " + seconds + " second" + (seconds > 1 ? "s" : "") : "";
            return (fDays + fHours + fMinutes + fSeconds).trim();
        }
    }

    public static String toString(long millis) {
        if(millis == Long.MAX_VALUE) {
            return "Permament";
        }
        millis -= System.currentTimeMillis();
        if(millis <= 0) {
            return "Expired";
        }
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis)
                - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(hours)
                - TimeUnit.DAYS.toMinutes(days);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.HOURS.toSeconds(hours)
                - TimeUnit.DAYS.toSeconds(days)
                - TimeUnit.MINUTES.toSeconds(minutes);
        List<String> format = new ArrayList<>();
        if(days > 0) {
            format.add(days + " days");
        }
        if(hours > 0) {
            format.add(hours + " hours");
        }
        if(minutes > 0) {
            format.add(minutes + " minutes");
        }
        if(seconds > 0) {
            format.add(seconds + " seconds");
        }
        return String.join(", ", format);
    }

    public static long fromString(String string) {
        if(string.equalsIgnoreCase("perm") || string.equalsIgnoreCase("permanent")) {
            return Long.MAX_VALUE;
        }
        long total = 0;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(string);
        while(matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
            switch (type) {
                case "y":
                    total += value * 60 * 60 * 24 * 365;
                case "M":
                    total += value * 60 * 60 * 24 * 30;
                case "w":
                    total += value * 60 * 60 * 24 * 7;
                case "d":
                    total += value * 60 * 60 * 24;
                case "h":
                    total += value * 60 * 60;
                case "m":
                    total += value * 60;
                    break;
                case "s":
                    total += value;
                    break;
            }
        }
        return total * 1000;
    }

    public static String scoreboardFormat(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

}