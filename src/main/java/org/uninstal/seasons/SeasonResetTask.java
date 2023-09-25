package org.uninstal.seasons;

import org.bukkit.Bukkit;
import org.uninstal.seasons.data.Season;

public class SeasonResetTask extends Thread {

    private final Seasons plugin;
    private final Season season;
    private final long next;

    public SeasonResetTask(Seasons plugin, Season season) {
        this.plugin = plugin;
        this.season = season;
        this.next = season.getEnd().getTimeInMillis();
        this.plugin.getLogger().info(String.format("Next cleaner start after %s days",
          (season.getEnd().getTimeInMillis() - System.currentTimeMillis()) / 86400000
        ));
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public Season getSeason() {
        return season;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(next - System.currentTimeMillis());
            plugin.getLogger().info("Season was ended, running cleaner...");
            Bukkit.getScheduler().runTask(plugin, plugin.getServices().getCleaner()::clean);
        } catch (InterruptedException e) {
            plugin.getLogger().info("Reset task was interrupted");
        }
    }
}