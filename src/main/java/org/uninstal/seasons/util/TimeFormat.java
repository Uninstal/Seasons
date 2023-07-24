package org.uninstal.seasons.util;

public class TimeFormat {
    
    public static String toFull(long time) {

        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        int s = seconds % 60;
        int m = minutes % 60;
        int h = hours % 24;
        int d = hours / 24;

        return d + "д " + h + "ч " + m + "м " + s + "с";
    }
    
    public static String toShort(long time) {

        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        int s = seconds % 60;
        int m = minutes % 60;
        int h = hours % 24;
        int d = hours / 24;

        String out = "";
        out += d == 0 ? "" : d + "д ";
        out += h == 0 ? "" : h + "ч ";
        out += m == 0 ? "" : m + "м ";
        out += s == 0 ? "" : s + "с ";
        return out.isEmpty() ? "0c" : out.trim();
    }
}