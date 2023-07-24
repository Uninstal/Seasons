package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.friends.db.Database;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class FriendsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseFriends")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        cleaner.getLogger().info("Dropping table in SQL (sync)...");
        Database.dropTable();
        return true;
    }
}
