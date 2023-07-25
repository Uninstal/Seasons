package org.uninstal.seasons.service.cleaner.impl;

import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class SeasonsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        cleaner.getLogger().info("Clearing table in SQL (sync)...");
        cleaner.getPlugin().getDatabase().clear();
        return true;
    }
}
