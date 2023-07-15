package org.uninstal.seasons.service.cleaner.impl;

import com.uninstal.clans.manager.Manager;
import org.bukkit.Bukkit;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class ClansCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseClans")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }
        
        cleaner.getLogger().info("Clearing of clans in memory...");
        Manager.getClans().forEach(clan -> {
            cleaner.getLogger().info(String.format("Deleting clan «%s»", clan.getName()));
            Manager.delete(clan);
        });
        return true;
    }
}