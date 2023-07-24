package org.uninstal.seasons.service.cleaner.impl;

import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class SeasonsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        cleaner.getLogger().info("Dropping table in SQL (sync)...");
        cleaner.getPlugin().getDatabase().dropTable();
        return true;
    }
}
