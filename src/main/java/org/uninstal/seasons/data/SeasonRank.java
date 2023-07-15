package org.uninstal.seasons.data;

public class SeasonRank {
    
    private final String display;
    private final int expTotal;
    private final int expToNext;

    public SeasonRank(String display, int expTotal, int expToNext) {
        this.display = display;
        this.expTotal = expTotal;
        this.expToNext = expToNext;
    }

    public int getExpToNext() {
        return expToNext;
    }

    public int getExpTotal() {
        return expTotal;
    }

    public String getDisplay() {
        return display;
    }
}