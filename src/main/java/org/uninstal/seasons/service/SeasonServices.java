package org.uninstal.seasons.service;

import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class SeasonServices {
    
    private final Seasons plugin;
    private final SeasonDataCleaner cleaner;
    private final SeasonRankService rankService;
    private final SeasonChestService chestService;
    private final SeasonUserService userService;
    
    public SeasonServices(Seasons plugin) {
        this.plugin = plugin;
        this.cleaner = new SeasonDataCleaner(plugin);
        this.rankService = new SeasonRankService(plugin);
        this.chestService = new SeasonChestService(plugin);
        this.userService = new SeasonUserService(plugin);
    }

    public SeasonChestService getChestService() {
        return chestService;
    }

    public SeasonRankService getRankService() {
        return rankService;
    }

    public SeasonDataCleaner getCleaner() {
        return cleaner;
    }

    public SeasonUserService getUserService() {
        return userService;
    }

    public Seasons getPlugin() {
        return plugin;
    }
}