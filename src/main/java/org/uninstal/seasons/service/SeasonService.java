package org.uninstal.seasons.service;

import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class SeasonService {
    
    private final Seasons plugin;
    private final SeasonDataCleaner cleaner;
    private final SeasonRankService rankService;
    private final SeasonChestService chestService;
    
    public SeasonService(Seasons plugin) {
        this.plugin = plugin;
        this.cleaner = new SeasonDataCleaner(plugin);
        this.rankService = new SeasonRankService(plugin);
        this.chestService = new SeasonChestService(plugin);
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

    public Seasons getPlugin() {
        return plugin;
    }
}