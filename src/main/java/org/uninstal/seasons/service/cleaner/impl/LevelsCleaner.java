package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.levels.data.Manager;
import org.uninstal.levels.db.Database;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class LevelsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseLevels")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        cleaner.getLogger().info("Clearing of users in memory...");
        Manager.clear();
        cleaner.getLogger().info("Dropping table in SQL (async)...");
        Database.dropTable();
        return true;
    }
}
