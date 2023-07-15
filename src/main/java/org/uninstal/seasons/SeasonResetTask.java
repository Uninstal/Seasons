package org.uninstal.seasons;

import java.util.Calendar;

public class SeasonResetTask extends Thread {
    
    private final Seasons plugin;
    private final long next;

    public SeasonResetTask(Seasons plugin, long seasonStart) {
        this.plugin = plugin;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(seasonStart);
        calendar.add(Calendar.DAY_OF_YEAR, 90);
        this.next = calendar.getTimeInMillis();
    }

    public Seasons getPlugin() {
        return plugin;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(next - System.currentTimeMillis());
            plugin.getLogger().info("Season was ended, running cleaner...");
            plugin.getServices().getCleaner().clean();
        } catch (InterruptedException e) {
            plugin.getLogger().info("Reset task was interrupted");
        }
    }
}