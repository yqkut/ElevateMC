package dev.apposed.prime.spigot.util.time;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.elevatemc.elib.util.TimeUtils;

public class DurationUtils {


    public static String formatAgo(long millis) {
        return TimeUtils.formatIntoDetailedString((int)millis / 1000) + " ago";
    }

    public static String toString(long millis) {
        if(millis == Long.MAX_VALUE) {
            return "Permanent";
        }
        millis -= System.currentTimeMillis();
        if(millis <= 0) {
            return "Expired";
        }
        int secs = (int) (millis / 1000);
        if (secs == 0) {
            return "0 seconds";
        }
        final int remainder = secs % 86400;
        final int days = secs / 86400;
        final int hours = remainder / 3600;
        final int minutes = remainder / 60 - hours * 60;
        final int seconds = remainder % 3600 - minutes * 60;
        final String fDays = (days > 0) ? (" " + days + " day" + ((days > 1) ? "s" : "")) : "";
        final String fHours = (hours > 0) ? (" " + hours + " hour" + ((hours > 1) ? "s" : "")) : "";
        final String fMinutes = (minutes > 0) ? (" " + minutes + " minute" + ((minutes > 1) ? "s" : "")) : "";
        final String fSeconds = (seconds > 0) ? (" " + seconds + " second" + ((seconds > 1) ? "s" : "")) : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static long fromString(final String time) {
        if(time.equalsIgnoreCase("perm") || time.equalsIgnoreCase("permanent")) {
            return Long.MAX_VALUE;
        }

        if (time.equals("0") || time.equals("")) {
            return 0;
        }

        final String[] lifeMatch = { "w", "d", "h", "m", "s" };
        final int[] lifeInterval = { 604800, 86400, 3600, 60, 1 };
        int seconds = -1;
        for (int i = 0; i < lifeMatch.length; ++i) {
            final Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time);
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0;
                }
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }
        if (seconds == -1) {
            return -1;
        }

        return seconds * 1000L;
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