package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.clans.points.Main;
import org.uninstal.clans.points.data.manager.PointManager;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class CapturesCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseCaptures")) {
            cleaner.getLogger().warning("Plugin is not enabled");
            return false;
        }
        
        PointManager manager = Main.getPointManager();
        manager.getPoints().forEach(point -> {
            cleaner.getLogger().info(String.format("Clearing members in «%s»", point.getRegionId()));
            point.setOwner(null);
            point.getRegion().getMembers().clear();
        });
        return true;
    }
}