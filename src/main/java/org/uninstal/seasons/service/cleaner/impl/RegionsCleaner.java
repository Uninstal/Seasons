package org.uninstal.seasons.service.cleaner.impl;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class RegionsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        for (String regionId : SeasonConfig.getCleanerRegions()) {
            RegionManager manager = WorldGuardPlugin.inst().getRegionManager(Bukkit.getWorld("world"));
            ProtectedRegion region = manager.getRegion(regionId);
            if (region != null) {
                cleaner.getLogger().info(String.format("Clearing region «%s»", region.getId()));
                region.getOwners().clear();
                region.getMembers().clear();
            } else {
                cleaner.getLogger().warning(String.format("Cannot get regin with id «%s»", regionId));
            }
        }
        return true;
    }
}