package com.elevatemc.elib.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class TimeUtils {
    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        } else {
            return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
        }
    }

    /**
     * Return the amount of seconds from milliseconds.
     * Note: We explicitly use 1000.0F (float) instead of 1000L (long).
     *
     * @param millis the amount of time in milliseconds
     * @return the seconds
     */
    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0F);
    }


    /**
     * Delegate to TimeUtils#formatIntoMMSS for backwards compat
     */
    public static String formatIntoHHMMSS(int secs) {
        return formatIntoMMSS(secs);
    }

    /**
     * Formats the time into a format of HH:MM:SS. Example: 3600 (1 hour) displays as '01:00:00'
     *
     * @param secs The input time, in seconds.
     * @return The HH:MM:SS formatted time.
     */
    public static String formatIntoMMSS(int secs) {
        // Calculate the seconds to display:
        int seconds = secs % 60;
        secs -= seconds;

        // Calculate the minutes:
        long minutesCount = secs / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;

        long hours = minutesCount / 60;
        return (hours > 0 ? (hours < 10 ? "0" : "") + hours + ":" : "") + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }


    public static String formatLongIntoMMSS(final long secs) {
        final int unconvertedSeconds = (int)secs;
        return formatIntoMMSS(unconvertedSeconds);
    }

    /**
     * Formats time into a detailed format. Example: 600 seconds (10 minutes) displays as '10 minutes'
     *
     * @param secs The input time, in seconds.
     * @return The formatted time.
     */
    public static String formatIntoDetailedString(int secs) {
        if (secs == 0) {
            return "0 seconds";
        }
        int remainder = secs % 86400;

        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        String fDays = (days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "");
        String fHours = (hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "");
        String fMinutes = (minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "");
        String fSeconds = (seconds > 0 ? " " + seconds + " second" + (seconds > 1 ? "s" : "") : "");

        return ((fDays + fHours + fMinutes + fSeconds).trim());
    }

    public static String formatLongIntoString(long millis) {
        return formatIntoString((int) millis / 1000);
    }

    public static String formatIntoString(int secs) {
        if (secs == 0) {
            return "0s";
        }

        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        String fDays = (days > 0 ? days + "d " : "");
        String fHours = (hours > 0 ? hours + "h " : "");
        String fMinutes = (minutes > 0 ? minutes + "m " : "");
        String fSeconds = (seconds > 0 ? seconds + "s " : "");

        return ((fDays + fHours + fMinutes + fSeconds).trim());
    }

    /**
     * Formats time into a format of MM/dd/yyyy HH:mm.
     *
     * @param date The Date instance to format.
     * @return The formatted time.
     */
    public static String formatIntoCalendarString(Date date) {
        return (dateFormat.format(date));
    }

    /**
     * Parses a string, such as '1h4m25s' into a number of seconds.
     *
     * @param time The string to attempt to parse.
     * @return The number of seconds 'in' the given string.
     */
    public static int parseTime(String time) {

        if (time.equals("0") || time.equals("")) {
            return (0);
        }

        String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
        int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
        int seconds = 0;

        for (int i = 0; i < lifeMatch.length; i++) {

            final Matcher matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(time);

            while (matcher.find()) {
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }

        }

        return (seconds);
    }

    /**
     * Gets the seconds between date A and date B. This will never return a negative number.
     *
     * @param a Date A
     * @param b Date B
     * @return The number of seconds between date A and date B.
     */
    public static int getSecondsBetween(Date a, Date b) {
        return (Math.abs((int) (a.getTime() - b.getTime()) / 1000));
    }

}